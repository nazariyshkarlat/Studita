package com.example.studita.presentation.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studita.App
import com.example.studita.R
import com.example.studita.di.data.AuthorizationModule
import com.example.studita.di.data.UserStatisticsModule
import com.example.studita.domain.entity.PushTokenData
import com.example.studita.domain.entity.authorization.AuthorizationRequestData
import com.example.studita.domain.entity.authorization.LogInResponseData
import com.example.studita.domain.interactor.LogInStatus
import com.example.studita.domain.interactor.SignUpStatus
import com.example.studita.domain.validator.AuthorizationValidator
import com.example.studita.utils.DeviceUtils
import com.example.studita.utils.UserUtils
import com.example.studita.utils.launchBlock
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job

class AuthorizationFragmentViewModel : ViewModel() {

    val passwordFieldIsEmptyState = MutableLiveData<Boolean>()
    val passwordIsVisibleState = MutableLiveData<Boolean>()
    val authorizationState = SingleLiveEvent<AuthorizationResult>()
    val errorState = SingleLiveEvent<Int>()

    private var job: Job? = null

    private val userStatisticsInteractor = UserStatisticsModule.getUserStatisticsInteractorImpl()
    private val authorizationInteractor = AuthorizationModule.getAuthorizationInteractorImpl()

    fun setPasswordEmpty(password: String) {
        passwordFieldIsEmptyState.value = password.isEmpty()
    }

    fun changePasswordVisible(nowVisible: Boolean) {
        passwordIsVisibleState.value = !nowVisible
    }

    private fun validate(dates: Pair<String, String>): AuthorizationResult {
        val validator = AuthorizationValidator()
        val result =
            when {
                !validator.isValid(dates).first -> {
                    AuthorizationResult.IncorrectEmail
                }
                validator.isValid(dates) == true to false -> {
                    AuthorizationResult.PasswordLessMixLength
                }
                validator.isValid(dates) == true to true -> {
                    AuthorizationResult.Valid
                }
                else -> throw UnsupportedOperationException("unknown authorization result")
            }
        if (result != AuthorizationResult.Valid)
            authorizationState.value = result
        return result
    }

    fun logIn(dates: Pair<String, String>, applicationRef: Application) {
        if (validate(dates) == AuthorizationResult.Valid) {
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("FIREBASE", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    val token = task.result?.token

                    job = GlobalScope.launchBlock(job) {
                        when (val result = authorizationInteractor.logIn(
                            AuthorizationRequestData(
                                dates.first,
                                dates.second,
                                null,
                                null,
                                token?.let {
                                    PushTokenData(
                                        it,
                                        DeviceUtils.getDeviceId(applicationRef)
                                    )
                                }
                            )
                        )) {
                            is LogInStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                            is LogInStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                            is LogInStatus.Failure -> authorizationState.postValue(
                                AuthorizationResult.LogInFailure
                            )
                            is LogInStatus.NoUserFound -> authorizationState.postValue(
                                AuthorizationResult.NoUserFound
                            )
                            is LogInStatus.Success -> {
                                App.initUserData(result.result.userDataData)

                                authorizationState.value =
                                    AuthorizationResult.LogInSuccess(result.result)
                            }
                        }
                    }
                })
        }
    }

    fun signUp(dates: Pair<String, String>) {
        if (validate(dates) == AuthorizationResult.Valid) {
            job = GlobalScope.launchBlock(job) {
                when (authorizationInteractor.signUp(
                    AuthorizationRequestData(
                        dates.first,
                        dates.second,
                        if (!UserUtils.isLoggedIn()) UserUtils.userData else null,
                        if (!UserUtils.isLoggedIn()) userStatisticsInteractor.getUserStatisticsRecords() else null,
                        null
                    )
                )) {
                    is SignUpStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                    is SignUpStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                    is SignUpStatus.Failure -> authorizationState.postValue(AuthorizationResult.SignUpFailure)
                    is SignUpStatus.UserAlreadyExists -> authorizationState.postValue(
                        AuthorizationResult.UserAlreadyExists
                    )
                    is SignUpStatus.Success -> authorizationState.postValue(
                        AuthorizationResult.SignUpSuccess(
                            dates.first,
                            dates.second
                        )
                    )
                }
            }
        }
    }

    sealed class AuthorizationResult {
        object IncorrectEmail : AuthorizationResult()
        object PasswordLessMixLength : AuthorizationResult()
        object Valid : AuthorizationResult()
        object LogInFailure : AuthorizationResult()
        object SignUpFailure : AuthorizationResult()
        object NoUserFound : AuthorizationResult()
        object UserAlreadyExists : AuthorizationResult()
        data class LogInSuccess(val result: LogInResponseData) : AuthorizationResult()
        data class SignUpSuccess(val email: String, val password: String) : AuthorizationResult()

    }

}