package com.example.studita.data.repository.datasource.user_statistics

import com.example.studita.data.database.user_statistics.UserStatisticsDao
import com.example.studita.data.entity.UserStatisticsEntity
import com.example.studita.data.entity.UserTokenId
import com.example.studita.data.net.UserStatisticsService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.date.DateTimeFormat
import com.example.studita.domain.enum.UserStatisticsTime
import com.example.studita.domain.enum.timeToString
import com.example.studita.domain.exception.NetworkConnectionException
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class CloudUserStatisticsDataStore(private val connectionManager: ConnectionManager, private val userStatisticsService: UserStatisticsService, private val userStatisticsDao: UserStatisticsDao) : UserStatisticsDataStore{

    override suspend fun getUserStatisticsEntity(userTokenId: UserTokenId, time: UserStatisticsTime): Pair<Int, UserStatisticsEntity>{
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        }else {
            val timeStr = time.timeToString()
            val userStatisticsAsync = userStatisticsService.getUserStatisticsAsync(
                DateTimeFormat().format(
                    Date()
                ), timeStr, userTokenId
            )
            val result = userStatisticsAsync.await()
            val entity = result.body()!!
            userStatisticsDao.insertUserStatistics(entity)
           return result.code() to entity
        }
    }

}