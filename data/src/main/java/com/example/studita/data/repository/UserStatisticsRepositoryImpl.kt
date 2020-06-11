package com.example.studita.data.repository

import com.example.studita.data.entity.toBusinessEntity
import com.example.studita.data.entity.toRawEntity
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.repository.datasource.user_statistics.*
import com.example.studita.domain.entity.*
import com.example.studita.domain.repository.UserStatisticsRepository

class UserStatisticsRepositoryImpl(private val userStatisticsJsonDataStoreFactory: UserStatisticsJsonDataStoreFactory,
                                   private val connectionManager: ConnectionManager) : UserStatisticsRepository{

    override suspend fun getUserStatistics(userId: Int): Pair<Int, List<UserStatisticsData>> {
        val pair =  UserStatisticsDataStoreImpl(userStatisticsJsonDataStoreFactory.create(if(connectionManager.isNetworkAbsent()) UserStatisticsJsonDataStoreFactory.Priority.CACHE else UserStatisticsJsonDataStoreFactory.Priority.CLOUD)).getUserStatisticsEntity(userId)
        return pair.first to pair.second.map { it.toBusinessEntity() }
    }

    override suspend fun saveUserStatistics(userStatisticsRowData: UserStatisticsRowData) {
        (userStatisticsJsonDataStoreFactory.create(UserStatisticsJsonDataStoreFactory.Priority.CACHE) as DiskUserStatisticsJsonDataStore).saveUserStatisticsRecord(
                userStatisticsRowData.toRawEntity())
    }

    override suspend fun getUserStatisticsRecords(): List<UserStatisticsRowData>? {
        val userStatisticsRowEntities = (userStatisticsJsonDataStoreFactory.create(UserStatisticsJsonDataStoreFactory.Priority.CACHE) as DiskUserStatisticsJsonDataStore).getUserStatisticsRecords()
        return userStatisticsRowEntities?.map { it.toBusinessEntity() }
    }

}