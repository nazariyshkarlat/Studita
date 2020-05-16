package com.example.studita.presentation.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.authenticator.AccountAuthenticator
import com.example.studita.di.data.AuthorizationModule
import com.example.studita.di.data.UserStatisticsModule
import com.example.studita.domain.entity.authorization.SignInWithGoogleRequestData
import com.example.studita.domain.interactor.SignInWithGoogleStatus
import com.example.studita.utils.UserUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch
import java.lang.UnsupportedOperationException

class MainMenuFragmentViewModel : ViewModel(){

    val signUpMethodState = SingleLiveEvent<SignUpMethod>()
    val googleSignInState= SingleLiveEvent<Boolean>()

    private val userStatisticsInteractor = UserStatisticsModule.getUserStatisticsInteractorImpl()
    private val authorizationInteractor = AuthorizationModule.getAuthorizationInteractorImpl()


    fun onSignUpLogInClick(viewId: Int){
        when(viewId) {
            R.id.mainMenuWithGoogleButton -> signUpMethodState.postValue(SignUpMethod.WITH_GOOGLE)
            R.id.mainMenuUseEmailButton -> signUpMethodState.postValue(SignUpMethod.USE_EMAIL)
            else -> throw UnsupportedOperationException("unknown view id")
        }
    }

    fun signIn(context: Context): GoogleSignInClient{
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.resources.getString(R.string.server_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    fun handleSignInResult(task: Task<GoogleSignInAccount>, context: Context){
        try{
            val account = task.getResult(ApiException::class.java)
            viewModelScope.launch {
                account?.let {
                    if(authorizationInteractor.signInWithGoogle(SignInWithGoogleRequestData(account.idToken.toString(), if(!UserUtils.isLoggedIn()) UserUtils.userData else null, if(!UserUtils.isLoggedIn()) userStatisticsInteractor.getUserStatisticsRecords() else null))is SignInWithGoogleStatus.Success){
                        AccountAuthenticator.addAccount(context, it.email.toString())
                        googleSignInState.postValue(true)
                    }
                }
            }
        }catch (e: ApiException){
            throw e
        }
    }

    enum class SignUpMethod{
        WITH_GOOGLE,
        USE_EMAIL
    }
}