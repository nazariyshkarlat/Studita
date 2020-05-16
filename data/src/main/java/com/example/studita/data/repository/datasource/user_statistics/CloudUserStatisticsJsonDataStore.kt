package com.example.studita.data.repository.datasource.user_statistics

import com.example.studita.data.cache.user_statistics.UserStatisticsCache
import com.example.studita.data.entity.SaveUserStatisticsRequest
import com.example.studita.data.entity.UserIdToken
import com.example.studita.domain.date.DateTimeFormat
import com.example.studita.data.net.UserStatisticsService
import com.example.studita.data.net.connection.ConnectionManager
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
                val userStatistics = userStatisticsService.getUserStatistics(
                    DateTimeFormat().format(Date()),
                    userIdToken
                )
                userStatisticsJson = userStatistics.body()!!.toString()
                if (userStatisticsJson.isNotEmpty())
                    userStatisticsCache.saveUserStatisticsJson(userStatisticsJson)
                val statusCode = userStatistics.code()
                statusCode to userStatisticsJson
            } catch (e: Exception) {
                throw ServerUnavailableException()
            }
        }
}