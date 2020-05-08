package com.example.studita.data.repository.datasource.user_statistics

import com.example.studita.data.cache.user_statistics.UserStatisticsCache
import com.example.studita.data.entity.SaveUserStatisticsRequest
import com.example.studita.data.entity.UserIdToken
import com.example.studita.domain.date.DateTimeFormat
import com.example.studita.data.net.UserStatisticsService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.enum.UserStatisticsTime
import com.example.studita.domain.enum.timeToString
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import java.lang.Exception
import java.util.*

class CloudUserStatisticsJsonDataStore(private val connectionManager: ConnectionManager, private val userStatisticsService: UserStatisticsService, private val userStatisticsCache: UserStatisticsCache)
    : UserStatisticsJsonDataStore {
    override suspend fun getUserStatisticsJson(userIdToken: UserIdToken) =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val userStatisticsJson: String
                val userStatisticsAsync = userStatisticsService.getUserStatisticsAsync(
                    DateTimeFormat().format(Date()),
                    userIdToken
                )
                val result = userStatisticsAsync.await()
                userStatisticsJson = result.body().toString()
                if (userStatisticsJson.isNotEmpty())
                    userStatisticsCache.saveUserStatisticsJson(userStatisticsJson)
                val statusCode = result.code()
                statusCode to userStatisticsJson
            } catch (e: Exception) {
                throw ServerUnavailableException()
            }
        }

    suspend fun saveUserStatistics(saveUserStatisticsRequest: SaveUserStatisticsRequest) : Int{
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        }else {
            val saveUserStatisticsAsync = userStatisticsService.saveUserStatisticsAsync(saveUserStatisticsRequest)
            val result = saveUserStatisticsAsync.await()
            return result.code()
        }
    }
}