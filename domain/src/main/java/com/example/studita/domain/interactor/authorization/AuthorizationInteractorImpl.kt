package com.example.studita.domain.interactor.authorization

import com.example.studita.domain.entity.SignOutRequestData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.entity.authorization.AuthorizationRequestData
import com.example.studita.domain.entity.authorization.SignInWithGoogleRequestData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import com.example.studita.domain.interactor.*
import com.example.studita.domain.interactor.user_data.UserDataInteractor
import com.example.studita.domain.repository.AuthorizationRepository
import com.example.studita.domain.repository.UserDataRepository
import com.example.studita.domain.service.SyncSignOut
import kotlinx.coroutines.delay
import kotlin.time.seconds

class AuthorizationInteractorImpl(private val authorizationRepository: AuthorizationRepository, private val userDataRepository: UserDataRepository, private val syncSignOut: SyncSignOut) :
    AuthorizationInteractor {

    val retryDelay = 1000L

    override suspend fun checkTokenIsCorrect(
        userIdTokenData: UserIdTokenData,
        retryCount: Int
    ): CheckTokenIsCorrectStatus =
        try {
            val result = authorizationRepository.checkTokenIsCorrect(userIdTokenData)
            when (result.first) {
                200 -> if(result.second == true) CheckTokenIsCorrectStatus.Correct else CheckTokenIsCorrectStatus.Incorrect
                else -> CheckTokenIsCorrectStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException)
                        CheckTokenIsCorrectStatus.NoConnection
                    else
                        CheckTokenIsCorrectStatus.ServiceUnavailable
                } else {
                    if (e is NetworkConnectionException)
                        delay(retryDelay)
                    checkTokenIsCorrect(userIdTokenData, retryCount - 1)
                }
            } else
                CheckTokenIsCorrectStatus.Failure
        }

    override suspend fun signUp(authorizationRequestData: AuthorizationRequestData, retryCount: Int): SignUpStatus =
        try {
             when (authorizationRepository.signUp(authorizationRequestData)) {
                200 -> SignUpStatus.Success
                409 -> SignUpStatus.UserAlreadyExists
                else -> SignUpStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if(e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException)
                        SignUpStatus.NoConnection
                    else
                        SignUpStatus.ServiceUnavailable
                } else {
                    if (e is NetworkConnectionException)
                        delay(retryDelay)
                    signUp(authorizationRequestData, retryCount - 1)
                }
            }else
                SignUpStatus.Failure
        }

    override suspend fun logIn(authorizationRequestData: AuthorizationRequestData, retryCount: Int): LogInStatus =
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
            if(e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException)
                        LogInStatus.NoConnection
                    else
                        LogInStatus.ServiceUnavailable
                } else {
                    if (e is NetworkConnectionException)
                        delay(retryDelay)
                    logIn(authorizationRequestData, retryCount - 1)
                }
            }else
                LogInStatus.Failure
        }

    override suspend fun signInWithGoogle(signInWithGoogleRequestData: SignInWithGoogleRequestData, retryCount: Int): SignInWithGoogleStatus =
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
            if(e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException)
                        SignInWithGoogleStatus.NoConnection
                    else
                        SignInWithGoogleStatus.ServiceUnavailable
                } else {
                    if (e is NetworkConnectionException)
                        delay(retryDelay)
                    signInWithGoogle(signInWithGoogleRequestData, retryCount - 1)
                }
            }else
                SignInWithGoogleStatus.Failure
        }

    override suspend fun signOut(signOutRequestData: SignOutRequestData, retryCount: Int) {
        try {
            authorizationRepository.signOut(signOutRequestData)
        } catch (e: Exception) {
            e.printStackTrace()
            if(e is NetworkConnectionException || e is ServerUnavailableException) {
                when {
                    e is NetworkConnectionException -> {
                        syncSignOut.scheduleSignOut(signOutRequestData)
                    }
                    retryCount == 0 -> return
                    else -> {
                        signOut(signOutRequestData, retryCount - 1)
                    }
                }
            }
        }
    }

    override suspend fun deleteUserData() {
        userDataRepository.deleteUserData()
    }

}