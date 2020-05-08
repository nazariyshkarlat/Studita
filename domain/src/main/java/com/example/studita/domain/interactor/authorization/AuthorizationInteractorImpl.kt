package com.example.studita.domain.interactor.authorization

import com.example.studita.domain.entity.authorization.AuthorizationRequestData
import com.example.studita.domain.entity.authorization.SignInWithGoogleRequestData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.LogInStatus
import com.example.studita.domain.interactor.SignInWithGoogleStatus
import com.example.studita.domain.interactor.SignUpStatus
import com.example.studita.domain.repository.AuthorizationRepository
import com.example.studita.domain.repository.UserDataRepository

class AuthorizationInteractorImpl(private val authorizationRepository: AuthorizationRepository) :
    AuthorizationInteractor {
    override suspend fun signUp(authorizationRequestData: AuthorizationRequestData): SignUpStatus =
        try {
             when (authorizationRepository.signUp(authorizationRequestData)) {
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
            val logInResult = authorizationRepository.logIn(authorizationRequestData)
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

    override suspend fun signInWithGoogle(signInWithGoogleRequestData: SignInWithGoogleRequestData): SignInWithGoogleStatus =
        try {
            val signInWithGoogleResult = authorizationRepository.signInWithGoogle(signInWithGoogleRequestData)
            when (signInWithGoogleResult.first) {
                400 -> SignInWithGoogleStatus.Failure
                else -> signInWithGoogleResult.second?.let {
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