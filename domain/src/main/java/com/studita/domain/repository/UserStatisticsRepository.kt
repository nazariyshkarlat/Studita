package com.studita.domain.repository

import com.studita.domain.entity.UserStatisticsData
import com.studita.domain.entity.UserStatisticsRowData

interface UserStatisticsRepository {

    suspend fun getUserStatistics(userId: Int): Pair<Int, List<UserStatisticsData>>

    suspend fun saveUserStatistics(userStatisticsRowData: UserStatisticsRowData)

    suspend fun getUserStatisticsRecords(): List<UserStatisticsRowData>?

    suspend fun clearUserStatisticsRecords()
}