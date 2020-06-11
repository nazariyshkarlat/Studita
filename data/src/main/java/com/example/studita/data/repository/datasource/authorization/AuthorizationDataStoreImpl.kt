package com.example.studita.data.repository.datasource.authorization

import com.example.studita.data.cache.authentication.LogInCache
import com.example.studita.data.entity.AuthorizationRequestEntity
import com.example.studita.data.entity.LogInResponseEntity
import com.example.studita.data.entity.SignInWithGoogleRequest
import com.example.studita.data.entity.UserIdToken
import com.example.studita.data.net.AuthorizationService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException

class AuthorizationDataStoreImpl(private val connectionManager: ConnectionManager, private val authorizationService: AuthorizationService, private val logInCache: LogInCache) :
    AuthorizationDataStore {
    override suspend fun tryLogIn(authorizationRequestEntity: AuthorizationRequestEntity): Pair<Int, LogInResponseEntity?> =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            val logInResult: LogInResponseEntity?
            try {
                val logIn = authorizationService.logIn(authorizationRequestEntity)
                logInResult = logIn.body()
                val statusCode = logIn.code()
                if(logInResult != null)
                    logInCache.saveUserAuthenticationInfo(logInResult.userId, logInResult.userToken)
                statusCode to logInResult
            } catch (exception: Exception) {
                throw ServerUnavailableException()
            }
    }

    override suspend fun trySignUp(authorizationRequestEntity: AuthorizationRequestEntity): Int =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        }else{
            try {
                val signUp = authorizationService.signUp(authorizationRequestEntity)
                signUp.code()
            } catch (exception: Exception) {
                throw ServerUnavailableException()
            }
        }

    override suspend fun trySignOut(userIdToken: UserIdToken): Int{
        logInCache.clearUserAuthenticationInfo()
        return authorizationService.signOut(userIdToken).code()
    }

    override suspend fun trySignInWithGoogle(signInWithGoogleRequest: SignInWithGoogleRequest): Pair<Int, LogInResponseEntity?> =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        }else {
            val signInResult: LogInResponseEntity?
            try {
                val signIn =
                    authorizationService.signInWithGoogle(signInWithGoogleRequest)
                signInResult = signIn.body()
                if(signInResult != null)
                    logInCache.saveUserAuthenticationInfo(signInResult.userId, signInResult.userToken)
                val statusCode = signIn.code()
                statusCode to signInResult
            } catch (exception: Exception) {
                throw ServerUnavailableException()
            }
        }

}