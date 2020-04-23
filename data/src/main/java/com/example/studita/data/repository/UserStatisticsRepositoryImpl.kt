package com.example.studita.data.repository

import com.example.studita.data.entity.UserStatisticsEntity
import com.example.studita.data.entity.mapper.UserStatisticsDataMapper
import com.example.studita.data.entity.mapper.UserIdTokenMapper
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.repository.datasource.user_statistics.UserStatisticsDataStoreImpl
import com.example.studita.data.repository.datasource.user_statistics.UserStatisticsJsonDataStoreFactory
import com.example.studita.data.repository.datasource.user_statistics.UserStatisticsJsonDataStoreFactoryImpl
import com.example.studita.domain.entity.UserStatisticsData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.enum.UserStatisticsTime
import com.example.studita.domain.repository.UserStatisticsRepository

class UserStatisticsRepositoryImpl(private val userStatisticsJsonDataStoreFactory: UserStatisticsJsonDataStoreFactory, private val userStatisticsDataMapper: UserStatisticsDataMapper, private val connectionManager: ConnectionManager, private val userIdTokenMapper: UserIdTokenMapper) : UserStatisticsRepository{
    override suspend fun getUserStatistics(
        userIdToken: UserIdTokenData
    ): Pair<Int, List<UserStatisticsData>> {
        val pair =  UserStatisticsDataStoreImpl(userStatisticsJsonDataStoreFactory.create(if(connectionManager.isNetworkAbsent()) UserStatisticsJsonDataStoreFactory.Priority.CACHE else UserStatisticsJsonDataStoreFactory.Priority.CLOUD)).getUserStatisticsEntity(userIdTokenMapper.map(userIdToken))
        return pair.first to userStatisticsDataMapper.map(pair.second)
    }

}