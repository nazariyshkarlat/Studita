package com.example.studita.data.repository.datasource.authorization

import com.example.studita.data.cache.authentication.LogInCache
import com.example.studita.data.entity.*
import com.example.studita.data.net.AuthorizationService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import kotlinx.coroutines.CancellationException

class AuthorizationDataStoreImpl(
    private val connectionManager: ConnectionManager,
    private val authorizationService: AuthorizationService,
    private val logInCache: LogInCache
) :
    AuthorizationDataStore {
    override suspend fun tryCheckTokenIsCorrect(userIdToken: UserIdToken): Pair<Int, Boolean?> =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            val checkTokenIsCorrectResult: Boolean?
            try {
                val tokenIsCorrect = authorizationService.checkTokenIsCorrect(userIdToken)
                checkTokenIsCorrectResult = tokenIsCorrect.body()
                val statusCode = tokenIsCorrect.code()
                statusCode to checkTokenIsCorrectResult
            } catch (exception: Exception) {
                throw ServerUnavailableException()
            }
        }

    override suspend fun tryLogIn(authorizationRequestEntity: AuthorizationRequestEntity): Pair<Int, LogInResponseEntity?> =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            val logInResult: LogInResponseEntity?
            try {
                val logIn = authorizationService.logIn(authorizationRequestEntity)
                logInResult = logIn.body()
                val statusCode = logIn.code()
                if (logInResult != null)
                    logInCache.saveUserAuthenticationInfo(logInResult.userId, logInResult.userToken)
                statusCode to logInResult
            } catch (exception: Exception) {
                throw ServerUnavailableException()
            }
        }

    override suspend fun trySignUp(authorizationRequestEntity: AuthorizationRequestEntity): Int =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val signUp = authorizationService.signUp(authorizationRequestEntity)
                signUp.code()
            } catch (e: Exception) {
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }

    override suspend fun trySignOut(signOutRequestEntity: SignOutRequestEntity): Int {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val signOutCode = authorizationService.signOut(signOutRequestEntity).code()
                if (signOutCode == 200)
                    logInCache.clearUserAuthenticationInfo()
                return signOutCode
            } catch (e: Exception) {
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }
    }

    override suspend fun trySignInWithGoogle(signInWithGoogleRequest: SignInWithGoogleRequest): Pair<Int, LogInResponseEntity?> =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            val signInResult: LogInResponseEntity?
            try {
                val signIn =
                    authorizationService.signInWithGoogle(signInWithGoogleRequest)
                signInResult = signIn.body()
                if (signInResult != null)
                    logInCache.saveUserAuthenticationInfo(
                        signInResult.userId,
                        signInResult.userToken
                    )
                val statusCode = signIn.code()
                statusCode to signInResult
            } catch (e: Exception) {
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }

}