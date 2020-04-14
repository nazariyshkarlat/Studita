package com.example.studita.domain.interactor.authorization

import com.example.studita.domain.entity.authorization.AuthorizationRequestData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.LogInStatus
import com.example.studita.domain.interactor.SignInWithGoogleStatus
import com.example.studita.domain.interactor.SignUpStatus
import com.example.studita.domain.repository.AuthorizationRepository

class AuthorizationInteractorImpl(private val repository: AuthorizationRepository) :
    AuthorizationInteractor {
    override suspend fun signUp(authorizationRequestData: AuthorizationRequestData): SignUpStatus =
        try {
             when (repository.signUp(authorizationRequestData)) {
                200 -> SignUpStatus.Success
                409 -> SignUpStatus.UserAlreadyExists
                else -> SignUpStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException)
                SignUpStatus.NoConnection
            else
                SignUpStatus.ServiceUnavailable
        }

    override suspend fun logIn(authorizationRequestData: AuthorizationRequestData): LogInStatus =
        try {
            val logInResult = repository.logIn(authorizationRequestData)
            when (logInResult.first) {
                400 -> LogInStatus.Failure
                404 -> LogInStatus.NoUserFound
                else -> logInResult.second?.let {
                    LogInStatus.Success(
                        it
                    )
                } ?: LogInStatus.Failure
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException)
                LogInStatus.NoConnection
            else
                LogInStatus.ServiceUnavailable
        }

    override suspend fun signInWithGoogle(idToken: String): SignInWithGoogleStatus =
        try {
            val result = repository.signInWithGoogle(idToken)
            when (result.first) {
                400 -> SignInWithGoogleStatus.Failure
                else -> result.second?.let {
                    SignInWithGoogleStatus.Success(
                        it
                    )
                } ?: SignInWithGoogleStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException)
                SignInWithGoogleStatus.NoConnection
            else
                SignInWithGoogleStatus.ServiceUnavailable
        }

}