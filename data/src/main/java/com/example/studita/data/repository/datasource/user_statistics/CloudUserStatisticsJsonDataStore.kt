package com.example.studita.data.repository.datasource.user_statistics

import com.example.studita.data.net.UserStatisticsService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.date.DateTimeFormat
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import kotlinx.coroutines.CancellationException
import java.util.*

class CloudUserStatisticsJsonDataStore(
    private val connectionManager: ConnectionManager,
    private val userStatisticsService: UserStatisticsService
) : UserStatisticsJsonDataStore {
    override suspend fun getUserStatisticsJson(userId: Int) =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val userStatisticsJson: String
                val userStatistics = userStatisticsService.getUserStatistics(
                    DateTimeFormat().format(Date()),
                    userId
                )
                userStatisticsJson = userStatistics.body()!!.toString()
                val statusCode = userStatistics.code()
                statusCode to userStatisticsJson
            } catch (e: Exception) {
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }
}