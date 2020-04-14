package com.example.studita.data.repository.datasource.user_statistics

import com.example.studita.data.cache.user_statistics.UserStatisticsCache
import com.example.studita.domain.date.DateTimeFormat
import com.example.studita.data.entity.UserTokenId
import com.example.studita.data.net.UserStatisticsService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.enum.UserStatisticsTime
import com.example.studita.domain.enum.timeToString
import com.example.studita.domain.exception.NetworkConnectionException
import java.util.*

class CloudUserStatisticsJsonDataStore(private val connectionManager: ConnectionManager, private val userStatisticsService: UserStatisticsService, private val userStatisticsCache: UserStatisticsCache)
    : UserStatisticsJsonDataStore {
    override suspend fun getUserStatisticsJson(userTokenId: UserTokenId, userStatisticsTime: UserStatisticsTime) =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        }else {
            val userStatisticsJson: String
            val timeStr =userStatisticsTime.timeToString()
            val logInAsync = userStatisticsService.getUserStatisticsAsync(DateTimeFormat().format(Date()), timeStr, userTokenId)
            val result = logInAsync.await()
            userStatisticsJson = result.body().toString()
            if(userStatisticsJson.isNotEmpty())
                userStatisticsCache.saveUserStatisticsJson(userStatisticsTime, userStatisticsJson)
            val statusCode = result.code()
            statusCode to userStatisticsJson
        }
}