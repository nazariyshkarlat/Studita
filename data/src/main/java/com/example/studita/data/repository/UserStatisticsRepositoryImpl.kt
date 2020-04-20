package com.example.studita.data.repository

import com.example.studita.data.entity.mapper.UserStatisticsDataMapper
import com.example.studita.data.entity.mapper.UserTokenIdMapper
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.repository.datasource.user_statistics.UserStatisticsDataStoreFactory
import com.example.studita.data.repository.datasource.user_statistics.CloudUserStatisticsDataStore
import com.example.studita.data.repository.datasource.user_statistics.UserStatisticsDataStoreFactoryImpl
import com.example.studita.domain.entity.UserStatisticsData
import com.example.studita.domain.entity.UserTokenIdData
import com.example.studita.domain.enum.UserStatisticsTime
import com.example.studita.domain.repository.UserStatisticsRepository

class UserStatisticsRepositoryImpl(private val userStatisticsDataStoreFactory: UserStatisticsDataStoreFactoryImpl, private val userStatisticsDataMapper: UserStatisticsDataMapper, private val connectionManager: ConnectionManager)  : UserStatisticsRepository{
    override suspend fun getUserStatistics(
        userTokenId: UserTokenIdData,
        time: UserStatisticsTime
    ): Pair<Int, UserStatisticsData> {
        val pair =  userStatisticsDataStoreFactory.create(if(connectionManager.isNetworkAbsent()) UserStatisticsDataStoreFactory.Priority.CACHE else UserStatisticsDataStoreFactory.Priority.CLOUD).getUserStatisticsEntity(UserTokenIdMapper().map(userTokenId), time)
        return pair.first to userStatisticsDataMapper.map(pair.second)
    }

}