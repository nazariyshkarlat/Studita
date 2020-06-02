package com.example.studita.data.repository

import com.example.studita.data.entity.mapper.*
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.repository.datasource.user_statistics.*
import com.example.studita.domain.entity.*
import com.example.studita.domain.repository.UserStatisticsRepository

class UserStatisticsRepositoryImpl(private val userStatisticsJsonDataStoreFactory: UserStatisticsJsonDataStoreFactory,
                                   private val userStatisticsDataMapper: UserStatisticsDataMapper,
                                   private val connectionManager: ConnectionManager,
                                   private val userIdTokenMapper: UserIdTokenMapper,
                                   private val userStatisticsRowEntityMapper: UserStatisticsRowEntityMapper,
                                   private val userStatisticsRowDataMapper: UserStatisticsRowDataMapper) : UserStatisticsRepository{

    override suspend fun getUserStatistics(userId: Int): Pair<Int, List<UserStatisticsData>> {
        val pair =  UserStatisticsDataStoreImpl(userStatisticsJsonDataStoreFactory.create(if(connectionManager.isNetworkAbsent()) UserStatisticsJsonDataStoreFactory.Priority.CACHE else UserStatisticsJsonDataStoreFactory.Priority.CLOUD)).getUserStatisticsEntity(userId)
        return pair.first to userStatisticsDataMapper.map(pair.second)
    }

    override suspend fun saveUserStatistics(userStatisticsRowData: UserStatisticsRowData) {
        (userStatisticsJsonDataStoreFactory.create(UserStatisticsJsonDataStoreFactory.Priority.CACHE) as DiskUserStatisticsJsonDataStore).saveUserStatisticsRecord(
                userStatisticsRowEntityMapper.map(userStatisticsRowData))
    }

    override suspend fun getUserStatisticsRecords(): List<UserStatisticsRowData>? {
        val userStatisticsRowEntities = (userStatisticsJsonDataStoreFactory.create(UserStatisticsJsonDataStoreFactory.Priority.CACHE) as DiskUserStatisticsJsonDataStore).getUserStatisticsRecords()
        return userStatisticsRowEntities?.map { userStatisticsRowDataMapper.map(it) }
    }

}