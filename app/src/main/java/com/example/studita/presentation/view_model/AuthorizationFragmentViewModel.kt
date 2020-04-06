package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.di.data.AuthorizationModule
import com.example.studita.domain.entity.authorization.AuthorizationRequestData
import com.example.studita.domain.entity.authorization.LogInResponseData
import com.example.studita.domain.interactor.LogInStatus
import com.example.studita.domain.interactor.SignUpStatus
import com.example.studita.domain.validator.AuthorizationValidator
import com.example.studita.presentation.utils.launchExt
import kotlinx.coroutines.Job
import java.lang.UnsupportedOperationException

class AuthorizationFragmentViewModel : ViewModel(){

    val passwordFieldIsEmptyState = MutableLiveData<Boolean>()
    val passwordIsVisibleState = MutableLiveData<Boolean>()
    val authorizationState = MutableLiveData<AuthorizationResult>()
    val errorState = SingleLiveEvent<Int>()

    private var job: Job? = null
    private val interactor = AuthorizationModule.getAuthorizationInteractorImpl()

    fun setPasswordEmpty(password: String){
        passwordFieldIsEmptyState.value = password.isEmpty()
    }

    fun changePasswordVisible(nowVisible: Boolean){
        passwordIsVisibleState.value = !nowVisible
    }

    private fun validate(dates : Pair<String, String>): AuthorizationResult{
        val validator = AuthorizationValidator()
        val result =
            when {
                !validator.isValid(dates).first -> {
                    AuthorizationResult.IncorrectEmail
                }
                validator.isValid(dates) == true to false -> {
                    AuthorizationResult.PasswordLess6
                }
                validator.isValid(dates) == true to true -> {
                    AuthorizationResult.Valid
                }
                else -> throw UnsupportedOperationException("unknown authorization result")
            }
        if(result != AuthorizationResult.Valid)
            authorizationState.value = result
        return result
    }

    fun logIn(dates : Pair<String, String>){
        if(validate(dates) == AuthorizationResult.Valid){
            job = viewModelScope.launchExt(job){
            when(val result = interactor.logIn(
                AuthorizationRequestData(
                    dates.first,
                    dates.second
                )
            )){
                is LogInStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                is LogInStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                is LogInStatus.Failure -> authorizationState.value = AuthorizationResult.LogInFailure
                is LogInStatus.NoUserFound -> authorizationState.value = AuthorizationResult.NoUserFound
                is LogInStatus.Success -> authorizationState.value = AuthorizationResult.LogInSuccess(result.result)
            }}
        }
    }

    fun signUp(dates : Pair<String, String>){
        if(validate(dates) == AuthorizationResult.Valid){
            job = viewModelScope.launchExt(job){
                when(val result = interactor.signUp(
                    AuthorizationRequestData(
                        dates.first,
                        dates.second
                    )
                )){
                    is SignUpStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                    is SignUpStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                    is SignUpStatus.Failure -> authorizationState.value = AuthorizationResult.SignUpFailure
                    is SignUpStatus.UserAlreadyExists -> authorizationState.value = AuthorizationResult.UserAlreadyExists
                    is SignUpStatus.Success -> authorizationState.value = AuthorizationResult.SignUpSuccess(dates.first, dates.second)
                }}
        }
    }

    sealed class AuthorizationResult{
        object IncorrectEmail : AuthorizationResult()
        object PasswordLess6 : AuthorizationResult()
        object Valid : AuthorizationResult()
        object LogInFailure : AuthorizationResult()
        object SignUpFailure : AuthorizationResult()
        object NoUserFound : AuthorizationResult()
        object UserAlreadyExists : AuthorizationResult()
        data class LogInSuccess(val result: LogInResponseData) : AuthorizationResult()
        data class  SignUpSuccess(val email: String, val password: String) : AuthorizationResult()

    }

}