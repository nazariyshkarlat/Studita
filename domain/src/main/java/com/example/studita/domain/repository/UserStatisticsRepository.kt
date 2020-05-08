package com.example.studita.domain.repository

import com.example.studita.domain.entity.*
import com.example.studita.domain.enum.UserStatisticsTime

interface UserStatisticsRepository {

    suspend fun getUserStatistics(userIdToken: UserIdTokenData): Pair<Int, List<UserStatisticsData>>

    suspend fun saveUserStatistics(userStatisticsRowData: UserStatisticsRowData)

    suspend fun getUserStatisticsRecords(): List<UserStatisticsRowData>?
}