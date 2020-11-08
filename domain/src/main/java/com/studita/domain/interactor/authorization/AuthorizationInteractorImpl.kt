package com.studita.domain.interactor.authorization

import com.studita.domain.entity.SignOutRequestData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.entity.authorization.AuthorizationRequestData
import com.studita.domain.entity.authorization.SignInWithGoogleRequestData
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import com.studita.domain.interactor.*
import com.studita.domain.repository.AuthorizationRepository
import com.studita.domain.repository.UserDataRepository
import com.studita.domain.service.SyncSignOut
import kotlinx.coroutines.delay

class AuthorizationInteractorImpl(
    private val authorizationRepository: AuthorizationRepository,
    private val userDataRepository: UserDataRepository,
    private val syncSignOut: SyncSignOut
) :
    AuthorizationInteractor {

    val retryDelay = 1000L

    override suspend fun checkTokenIsCorrect(
        userIdTokenData: UserIdTokenData,
        retryCount: Int
    ): CheckTokenIsCorrectStatus =
        try {
            val result = authorizationRepository.checkTokenIsCorrect(userIdTokenData)
            when (result.first) {
                200 -> if (result.second == true) CheckTokenIsCorrectStatus.Correct else CheckTokenIsCorrectStatus.Incorrect
                else -> CheckTokenIsCorrectStatus.Failure
            }
        } catch (e: Exception) {
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException)
                        CheckTokenIsCorrectStatus.NoConnection
                    else
                        CheckTokenIsCorrectStatus.ServiceUnavailable
                } else {
                    delay(retryDelay)
                    checkTokenIsCorrect(userIdTokenData, retryCount - 1)
                }
            } else
                CheckTokenIsCorrectStatus.Failure
        }

    override suspend fun signUp(
        authorizationRequestData: AuthorizationRequestData,
        retryCount: Int
    ): SignUpStatus =
        try {
            when (authorizationRepository.signUp(authorizationRequestData)) {
                200 -> SignUpStatus.Success
                409 -> SignUpStatus.UserAlreadyExists
                else -> SignUpStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException)
                        SignUpStatus.NoConnection
                    else
                        SignUpStatus.ServiceUnavailable
                } else {
                    delay(retryDelay)
                    signUp(authorizationRequestData, retryCount - 1)
                }
            } else
                SignUpStatus.Failure
        }

    override suspend fun logIn(
        authorizationRequestData: AuthorizationRequestData,
        retryCount: Int
    ): LogInStatus =
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
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException)
                        LogInStatus.NoConnection
                    else
                        LogInStatus.ServiceUnavailable
                } else {
                    delay(retryDelay)
                    logIn(authorizationRequestData, retryCount - 1)
                }
            } else
                LogInStatus.Failure
        }

    override suspend fun signInWithGoogle(
        signInWithGoogleRequestData: SignInWithGoogleRequestData,
        retryCount: Int
    ): SignInWithGoogleStatus =
        try {
            val signInWithGoogleResult =
                authorizationRepository.signInWithGoogle(signInWithGoogleRequestData)
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
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException)
                        SignInWithGoogleStatus.NoConnection
                    else
                        SignInWithGoogleStatus.ServiceUnavailable
                } else {
                    delay(retryDelay)
                    signInWithGoogle(signInWithGoogleRequestData, retryCount - 1)
                }
            } else
                SignInWithGoogleStatus.Failure
        }

    override suspend fun signOut(signOutRequestData: SignOutRequestData, retryCount: Int) {
        try {
            authorizationRepository.signOut(signOutRequestData)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException) {
                        syncSignOut.scheduleSignOut(signOutRequestData)
                    }
                }else {
                    delay(retryDelay)
                        signOut(signOutRequestData, retryCount - 1)
                }
            }
        }
    }

    override suspend fun deleteUserData() {
        userDataRepository.deleteUserData()
    }

}