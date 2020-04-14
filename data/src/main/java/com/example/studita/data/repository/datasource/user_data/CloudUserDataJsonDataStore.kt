package com.example.studita.data.repository.datasource.user_data

import com.example.studita.data.cache.user_data.UserDataCache
import com.example.studita.domain.date.DateTimeFormat
import com.example.studita.data.entity.UserTokenId
import com.example.studita.data.net.UserDataService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.exception.NetworkConnectionException
import java.util.*

class CloudUserDataJsonDataStore(private val connectionManager: ConnectionManager, private val userDataService: UserDataService, private val userDataCache: UserDataCache)
    : UserDataJsonDataStore{
    override suspend fun getUserDataJson(userTokenId: UserTokenId) =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        }else {
            val userDataJson: String
            val logInAsync = userDataService.getUserDataAsync(DateTimeFormat().format(Date()), userTokenId)
            val result = logInAsync.await()
            userDataJson = result.body().toString()
            if(userDataJson.isNotEmpty())
                userDataCache.saveUserDataJson(userDataJson)
            val statusCode = result.code()
            statusCode to userDataJson
        }
}