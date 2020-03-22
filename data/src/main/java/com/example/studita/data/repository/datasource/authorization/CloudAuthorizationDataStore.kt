package com.example.studita.data.repository.datasource.authorization

import com.example.studita.data.entity.AuthorizationRequestEntity
import com.example.studita.data.entity.LogInResponseEntity
import com.example.studita.data.net.AuthorizationService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import okhttp3.ResponseBody
import retrofit2.Response

class CloudAuthorizationDataStore(private val connectionManager: ConnectionManager, private val authorizationService: AuthorizationService) :
    AuthorizationDataStore {
    override suspend fun tryLogIn(authorizationRequestEntity: AuthorizationRequestEntity): Pair<Int, LogInResponseEntity?> =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            val logInResult: LogInResponseEntity?
            try {
                val logInAsync = authorizationService.logInAsync(authorizationRequestEntity)
                val result = logInAsync.await()
                logInResult = result.body()
                val statusCode = result.code()
                statusCode to logInResult
            } catch (exception: Exception) {
                throw ServerUnavailableException()
            }
    }

    override suspend fun trySignUp(authorizationRequestEntity: AuthorizationRequestEntity): Int =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        }else{
            val responseCode: Int
            try {
                val signUpAsync = authorizationService.signUpAsync(authorizationRequestEntity)
                val result = signUpAsync.await()
                responseCode = result.code()
            } catch (exception: Exception) {
                throw ServerUnavailableException()
            }
            responseCode
        }

    override suspend fun trySignInWithGoogle(idToken: String): Pair<Int, LogInResponseEntity?> =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        }else {
            val logInResult: LogInResponseEntity?
            try {
                val logInAsync =
                    authorizationService.signInWithGoogleAsync(hashMapOf("id_token" to idToken))
                val result = logInAsync.await()
                logInResult = result.body()
                val statusCode = result.code()
                statusCode to logInResult
            } catch (exception: Exception) {
                throw ServerUnavailableException()
            }
        }

}