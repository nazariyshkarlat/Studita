package com.studita.presentation.view_model

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.studita.App
import com.studita.R
import com.studita.domain.entity.PushTokenData
import com.studita.domain.entity.authorization.AuthorizationRequestData
import com.studita.domain.entity.authorization.LogInResponseData
import com.studita.domain.interactor.LogInStatus
import com.studita.domain.interactor.SignUpStatus
import com.studita.domain.validator.AuthorizationValidator
import com.studita.utils.DeviceUtils
import com.studita.utils.UserUtils
import com.studita.utils.launchBlock
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.studita.App.Companion.initUserData
import com.studita.domain.interactor.UserDataStatus
import com.studita.domain.interactor.authorization.AuthorizationInteractor
import com.studita.domain.interactor.user_data.UserDataInteractor
import com.studita.domain.interactor.user_statistics.UserStatisticsInteractor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import org.koin.core.context.GlobalContext
import org.koin.core.context.GlobalContext.get


class AuthorizationFragmentViewModel : ViewModel() {

    val passwordFieldIsEmptyState = MutableLiveData<Boolean>()
    val passwordIsVisibleState = MutableLiveData<Boolean>()
    val authorizationState = SingleLiveEvent<AuthorizationResult>()
    val errorEvent = SingleLiveEvent<Int>()
    val progressState = SingleLiveEvent<Boolean>()

    private var job: Job? = null

    private val userStatisticsInteractor: UserStatisticsInteractor by get().inject()
    private val authorizationInteractor = GlobalContext.get().get<AuthorizationInteractor>()

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

    fun logIn(dates: Pair<String, String>, applicationRef: Application, afterSignUp: Boolean) {
        if (validate(dates) == AuthorizationResult.Valid) {
            if(!afterSignUp)
                progressState.value = true
            FirebaseInstanceId.getInstance().instanceId.apply {
                addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        progressState.value = false
                        errorEvent.value = R.string.server_temporarily_unavailable
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
                            is LogInStatus.NoConnection -> errorEvent.value = R.string.no_connection
                            is LogInStatus.ServiceUnavailable -> errorEvent.value =
                                R.string.server_temporarily_unavailable
                            is LogInStatus.Failure -> authorizationState.value =
                                AuthorizationResult.LogInFailure
                            is LogInStatus.NoUserFound -> authorizationState.value =
                                AuthorizationResult.NoUserFound
                            is LogInStatus.Success -> {
                                App.userDataDeferred.complete(UserDataStatus.Success(result.result.userDataData))
                                initUserData(result.result.userDataData)

                                authorizationState.value =
                                    AuthorizationResult.LogInSuccess(
                                        result.result,
                                        dates.first,
                                        afterSignUp
                                    )
                            }
                        }
                        progressState.value = false
                    }
                })
                addOnFailureListener {
                    progressState.value = false
                    errorEvent.value = R.string.server_temporarily_unavailable
                }
                addOnCanceledListener {
                    progressState.value = false
                    errorEvent.value = R.string.server_temporarily_unavailable
                }
            }
        }
    }

    fun signUp(dates: Pair<String, String>) {
        if (validate(dates) == AuthorizationResult.Valid) {
            progressState.value = true
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
                    is SignUpStatus.NoConnection -> errorEvent.value = R.string.no_connection
                    is SignUpStatus.ServiceUnavailable -> errorEvent.value = R.string.server_temporarily_unavailable
                    is SignUpStatus.Failure -> authorizationState.value = AuthorizationResult.SignUpFailure
                    is SignUpStatus.UserAlreadyExists -> authorizationState.value =
                        AuthorizationResult.UserAlreadyExists
                    is SignUpStatus.Success -> {
                        userStatisticsInteractor.clearUserStaticsRecords()
                        authorizationState.value =
                            AuthorizationResult.SignUpSuccess(
                                dates.first,
                                dates.second
                            )
                    }
                }
                progressState.value = false
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
        data class LogInSuccess(val result: LogInResponseData, val email: String, val afterSignUp: Boolean) : AuthorizationResult()
        data class SignUpSuccess(val email: String, val password: String) : AuthorizationResult()
    }

    sealed class ErrorType{
        object EmailError: ErrorType()
        object PasswordError: ErrorType()
        object NoTypeError: ErrorType()
    }

}