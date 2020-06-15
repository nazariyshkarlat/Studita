package com.example.studita.data.repository.datasource.user_statistics

import com.example.studita.data.database.user_statistics.UserStatisticsDao
import com.example.studita.data.entity.UserIdToken
import com.example.studita.data.entity.UserStatisticsRowEntity
import com.example.studita.domain.enum.UserStatisticsTime
import com.example.studita.domain.enum.timeToString


class DiskUserStatisticsJsonDataStore(private val userStatisticsDao: UserStatisticsDao) {

    suspend fun saveUserStatisticsRecord(userStatisticsRowEntity: UserStatisticsRowEntity){
        userStatisticsDao.insertUserStatistics(userStatisticsRowEntity)
    }

    suspend fun getUserStatisticsRecords() : List<UserStatisticsRowEntity>?{
        val statistics = userStatisticsDao.getUserStatistics()
        userStatisticsDao.deleteUserStatistics()
        return statistics
    }
}