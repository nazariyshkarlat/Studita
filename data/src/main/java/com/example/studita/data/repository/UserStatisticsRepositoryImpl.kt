package com.example.studita.data.repository

import com.example.studita.data.entity.toBusinessEntity
import com.example.studita.data.entity.toRawEntity
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.repository.datasource.user_statistics.DiskUserStatisticsRecordsDataStore
import com.example.studita.data.repository.datasource.user_statistics.UserStatisticsDataStoreImpl
import com.example.studita.data.repository.datasource.user_statistics.UserStatisticsJsonDataStoreFactory
import com.example.studita.domain.entity.UserStatisticsData
import com.example.studita.domain.entity.UserStatisticsRowData
import com.example.studita.domain.repository.UserStatisticsRepository

class UserStatisticsRepositoryImpl(
    private val userStatisticsJsonDataStoreFactory: UserStatisticsJsonDataStoreFactory,
    private val diskUserStatisticsRecordsDataStore: DiskUserStatisticsRecordsDataStore,
    private val connectionManager: ConnectionManager
) : UserStatisticsRepository {

    override suspend fun getUserStatistics(userId: Int): Pair<Int, List<UserStatisticsData>> {
        val pair =
            UserStatisticsDataStoreImpl(userStatisticsJsonDataStoreFactory.create()).getUserStatisticsEntity(
                userId
            )
        return pair.first to pair.second.map { it.toBusinessEntity() }
    }

    override suspend fun saveUserStatistics(userStatisticsRowData: UserStatisticsRowData) {
        diskUserStatisticsRecordsDataStore.saveUserStatisticsRecord(
            userStatisticsRowData.toRawEntity()
        )
    }

    override suspend fun getUserStatisticsRecords(): List<UserStatisticsRowData>? {
        val userStatisticsRowEntities = diskUserStatisticsRecordsDataStore.getUserStatisticsRecords()
        return userStatisticsRowEntities?.map { it.toBusinessEntity() }
    }

    override suspend fun clearUserStatisticsRecords(){
        diskUserStatisticsRecordsDataStore.clearUserStatisticsRecords()
    }

}