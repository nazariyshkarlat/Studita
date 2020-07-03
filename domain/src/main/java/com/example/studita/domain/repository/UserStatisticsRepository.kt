package com.example.studita.domain.repository

import com.example.studita.domain.entity.*

interface UserStatisticsRepository {

    suspend fun getUserStatistics(userId: Int): Pair<Int, List<UserStatisticsData>>

    suspend fun saveUserStatistics(userStatisticsRowData: UserStatisticsRowData)

    suspend fun getUserStatisticsRecords(): List<UserStatisticsRowData>?
}