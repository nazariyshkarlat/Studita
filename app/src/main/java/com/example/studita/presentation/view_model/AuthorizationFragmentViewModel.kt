package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.di.AuthorizationModule
import com.example.studita.domain.entity.authorization.AuthorizationRequestData
import com.example.studita.domain.interactor.LogInStatus
import com.example.studita.domain.interactor.SignUpStatus
import com.example.studita.domain.validator.AuthorizationValidator
import com.example.studita.presentation.extensions.launchExt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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
                    AuthorizationResult.INCORRECT_EMAIL
                }
                validator.isValid(dates) == true to false -> {
                    AuthorizationResult.PASSWORD_LESS_6
                }
                validator.isValid(dates) == true to true -> {
                    AuthorizationResult.VALID
                }
                else -> throw UnsupportedOperationException("unknown authorization result")
            }
        if(result != AuthorizationResult.VALID)
            authorizationState.value = result
        return result
    }

    fun logIn(dates : Pair<String, String>){
        if(validate(dates) == AuthorizationResult.VALID){
            job = viewModelScope.launchExt(job){
            when(interactor.logIn(
                AuthorizationRequestData(
                    dates.first,
                    dates.second
                )
            )){
                is LogInStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                is LogInStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                is LogInStatus.Failure -> authorizationState.value = AuthorizationResult.LOG_IN_FAILURE
                is LogInStatus.NoUserFound -> authorizationState.value = AuthorizationResult.NO_USER_FOUND
                is LogInStatus.Success -> authorizationState.value = AuthorizationResult.LOG_IN_SUCCESS
            }}
        }
    }

    fun signUp(dates : Pair<String, String>){
        if(validate(dates) == AuthorizationResult.VALID){
            job = viewModelScope.launchExt(job){
                when(interactor.signUp(
                    AuthorizationRequestData(
                        dates.first,
                        dates.second
                    )
                )){
                    is SignUpStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                    is SignUpStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                    is SignUpStatus.Failure -> authorizationState.value = AuthorizationResult.SIGN_UP_FAILURE
                    is SignUpStatus.UserAlreadyExists -> authorizationState.value = AuthorizationResult.USER_ALREADY_EXISTS
                    is SignUpStatus.Success -> authorizationState.value = AuthorizationResult.SIGN_UP_SUCCESS
                }}
        }
    }

    enum class AuthorizationResult{
        INCORRECT_EMAIL,
        PASSWORD_LESS_6,
        VALID,
        LOG_IN_FAILURE,
        SIGN_UP_FAILURE,
        NO_USER_FOUND,
        USER_ALREADY_EXISTS,
        LOG_IN_SUCCESS,
        SIGN_UP_SUCCESS,

    }

}