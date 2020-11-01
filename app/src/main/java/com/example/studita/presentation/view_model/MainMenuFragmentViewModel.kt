package com.example.studita.presentation.view_model

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.App
import com.example.studita.R
import com.example.studita.authenticator.AccountAuthenticator
import com.example.studita.di.data.AuthorizationModule
import com.example.studita.di.data.UserStatisticsModule
import com.example.studita.domain.entity.PushTokenData
import com.example.studita.domain.entity.authorization.SignInWithGoogleRequestData
import com.example.studita.domain.interactor.SignInWithGoogleStatus
import com.example.studita.utils.DeviceUtils
import com.example.studita.utils.UserUtils
import com.example.studita.utils.launchExt
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainMenuFragmentViewModel : ViewModel() {

    val signUpMethodState = SingleLiveEvent<SignUpMethod>()
    val googleSignInState = SingleLiveEvent<SignInWithGoogleStatus>()
    val progressState = SingleLiveEvent<Boolean>()

    private val userStatisticsInteractor = UserStatisticsModule.getUserStatisticsInteractorImpl()
    private val authorizationInteractor = AuthorizationModule.getAuthorizationInteractorImpl()

    private var signInJob: Job? = null


    fun onSignUpLogInClick(viewId: Int) {
        when (viewId) {
            R.id.mainMenuWithGoogleButton -> signUpMethodState.value = SignUpMethod.WITH_GOOGLE
            R.id.mainMenuUseEmailButton -> signUpMethodState.value = SignUpMethod.USE_EMAIL
            else -> throw UnsupportedOperationException("unknown view id")
        }
    }

    fun signIn(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.resources.getString(R.string.server_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    fun handleSignInResult(task: Task<GoogleSignInAccount>, applicationRef: Application) {
        progressState.value = true
        try {
            var result : SignInWithGoogleStatus? = null
            val account = task.getResult(ApiException::class.java)
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { completeResult ->
                    if (!completeResult.isSuccessful) {
                        Log.w("FIREBASE", "getInstanceId failed", completeResult.exception)
                        googleSignInState.value = if(result is SignInWithGoogleStatus.Success) result else SignInWithGoogleStatus.Failure
                        progressState.value = false
                        return@OnCompleteListener
                    }

                    val token = completeResult.result?.token
                    if (token != null) {
                        signInJob = viewModelScope.launchExt(signInJob){
                            account?.let {
                                result = authorizationInteractor.signInWithGoogle(
                                    SignInWithGoogleRequestData(
                                        account.idToken.toString(),
                                        if (!UserUtils.isLoggedIn()) UserUtils.userData else null,
                                        if (!UserUtils.isLoggedIn()) userStatisticsInteractor.getUserStatisticsRecords() else null,
                                        PushTokenData(
                                            token,
                                            DeviceUtils.getDeviceId(applicationRef)
                                        )
                                    )
                                )
                                if (result is SignInWithGoogleStatus.Success) {
                                    userStatisticsInteractor.clearUserStaticsRecords()
                                    App.initUserData((result as SignInWithGoogleStatus.Success).result.userDataData)
                                    AccountAuthenticator.addAccount(
                                        applicationRef,
                                        it.email.toString()
                                    )
                                }
                            }
                            googleSignInState.value = if(result is SignInWithGoogleStatus.Success) result else SignInWithGoogleStatus.Failure
                            progressState.value = false
                        }
                    }

                })
        } catch (e: ApiException) {
            e.printStackTrace()
            googleSignInState.value = SignInWithGoogleStatus.Failure
            progressState.value = false
        }
    }

    enum class SignUpMethod {
        WITH_GOOGLE,
        USE_EMAIL
    }
}