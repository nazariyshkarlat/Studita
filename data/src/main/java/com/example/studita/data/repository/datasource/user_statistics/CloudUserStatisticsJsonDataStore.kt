package com.example.studita.data.repository.datasource.user_statistics

import com.example.studita.data.database.user_statistics.UserStatisticsCache
import com.example.studita.data.net.UserStatisticsService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.enum.UserStatisticsTime
import com.example.studita.domain.exception.NetworkConnectionException

class CloudUserStatisticsJsonDataStore(private val connectionManager: ConnectionManager, private val userStatisticsService: UserStatisticsService, private val userStatisticsCache: UserStatisticsCache)
    : UserStatisticsJsonDataStore {
    override suspend fun getUserStatisticsJson(userId: String, userToken: String, userStatisticsTime: UserStatisticsTime) =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        }else {
            val userStatisticsJson: String
            val timeStr = when(userStatisticsTime){
                UserStatisticsTime.TODAY -> "today"
                UserStatisticsTime.YESTERDAY -> "yesterday"
                UserStatisticsTime.WEEK -> "week"
                UserStatisticsTime.MONTH -> "month"
            }
            val logInAsync = userStatisticsService.getUserStatisticsAsync(timeStr, hashMapOf("user_id" to userId, "user_token" to userToken))
            val result = logInAsync.await()
            userStatisticsJson = result.body().toString()
            if(userStatisticsJson.isNotEmpty())
                userStatisticsCache.saveUserStatisticsJson(userStatisticsJson)
            val statusCode = result.code()
            statusCode to userStatisticsJson
        }
}