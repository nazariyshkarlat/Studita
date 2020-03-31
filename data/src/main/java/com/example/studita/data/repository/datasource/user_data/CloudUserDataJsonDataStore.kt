package com.example.studita.data.repository.datasource.user_data

import com.example.studita.data.database.user_data.UserDataCache
import com.example.studita.data.entity.UserDataEntity
import com.example.studita.data.net.UserDataService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.exception.NetworkConnectionException

class CloudUserDataJsonDataStore(private val connectionManager: ConnectionManager, private val userDataService: UserDataService, private val userDataCache: UserDataCache)
    : UserDataJsonDataStore{
    override suspend fun getUserDataJson(userId: String, userToken: String) =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        }else {
            val userDataJson: String
            val logInAsync = userDataService.getUserDataAsync(hashMapOf("user_id" to userId, "user_token" to userToken))
            val result = logInAsync.await()
            userDataJson = result.body().toString()
            if(userDataJson.isNotEmpty())
                userDataCache.saveUserDataJson(userDataJson)
            val statusCode = result.code()
            statusCode to userDataJson
        }
}