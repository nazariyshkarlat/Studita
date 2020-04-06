package com.example.studita.data.repository

import com.example.studita.data.entity.mapper.UserStatisticsDataMapper
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.repository.datasource.user_statistics.UserStatisticsDataStoreFactory
import com.example.studita.data.repository.datasource.user_statistics.UserStatisticsJsonDataStoreFactoryImpl
import com.example.studita.data.repository.datasource.user_statistics.UserStatisticsDataStoreImpl
import com.example.studita.domain.entity.UserStatisticsData
import com.example.studita.domain.enum.UserStatisticsTime
import com.example.studita.domain.repository.UserStatisticsRepository

class UserStatisticsRepositoryImpl(private val userStatisticsDataStoreFactory: UserStatisticsJsonDataStoreFactoryImpl, private val userStatisticsDataMapper: UserStatisticsDataMapper, private val connectionManager: ConnectionManager)  : UserStatisticsRepository{
    override suspend fun getUserStatistics(
        userId: String,
        userToken: String,
        time: UserStatisticsTime
    ): Pair<Int, UserStatisticsData> {
        val pair =  UserStatisticsDataStoreImpl(userStatisticsDataStoreFactory.create(if(connectionManager.isNetworkAbsent()) UserStatisticsDataStoreFactory.Priority.CACHE else UserStatisticsDataStoreFactory.Priority.CLOUD)).getUserStatisticsEntity(userId, userToken, time)
        return pair.first to userStatisticsDataMapper.map(pair.second)
    }


}