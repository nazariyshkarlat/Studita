package com.example.studita.domain.repository

import com.example.studita.domain.entity.UserStatisticsData
import com.example.studita.domain.entity.UserStatisticsRowData

interface UserStatisticsRepository {

    suspend fun getUserStatistics(userId: Int): Pair<Int, List<UserStatisticsData>>

    suspend fun saveUserStatistics(userStatisticsRowData: UserStatisticsRowData)

    suspend fun getUserStatisticsRecords(): List<UserStatisticsRowData>?

    suspend fun clearUserStatisticsRecords()
}