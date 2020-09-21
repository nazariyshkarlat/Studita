package com.example.studita.data.repository.datasource.user_statistics

import com.example.studita.data.database.user_statistics.UserStatisticsDao
import com.example.studita.data.entity.UserStatisticsRowEntity


class DiskUserStatisticsRecordsDataStore(private val userStatisticsDao: UserStatisticsDao) {

    suspend fun saveUserStatisticsRecord(userStatisticsRowEntity: UserStatisticsRowEntity) {
        userStatisticsDao.insertUserStatistics(userStatisticsRowEntity)
    }

    suspend fun getUserStatisticsRecords(): List<UserStatisticsRowEntity>? {
        return userStatisticsDao.getUserStatistics()
    }

    suspend fun clearUserStatisticsRecords(){
        userStatisticsDao.deleteUserStatistics()
    }
}