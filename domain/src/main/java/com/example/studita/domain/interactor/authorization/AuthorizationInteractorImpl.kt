package com.example.studita.domain.interactor.authorization

import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.entity.authorization.AuthorizationRequestData
import com.example.studita.domain.entity.authorization.SignInWithGoogleRequestData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.LogInStatus
import com.example.studita.domain.interactor.SignInWithGoogleStatus
import com.example.studita.domain.interactor.SignUpStatus
import com.example.studita.domain.interactor.user_data.UserDataInteractor
import com.example.studita.domain.repository.AuthorizationRepository
import com.example.studita.domain.repository.UserDataRepository
import com.example.studita.domain.service.SyncSignOut

class AuthorizationInteractorImpl(private val authorizationRepository: AuthorizationRepository, private val userDataRepository: UserDataRepository, private val syncSignOut: SyncSignOut) :
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
                    userDataRepository.saveUserData(it.userDataData)
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
                    userDataRepository.saveUserData(it.userDataData)
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

    override suspend fun signOut(userIdTokenData: UserIdTokenData) {
        try {
            userDataRepository.deleteUserData()
            authorizationRepository.signOut(userIdTokenData)
        }catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException)
                syncSignOut.scheduleSignOut()
            else
                SignInWithGoogleStatus.ServiceUnavailable
        }
    }

}