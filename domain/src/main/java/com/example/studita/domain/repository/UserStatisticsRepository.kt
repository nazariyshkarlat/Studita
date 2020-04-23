package com.example.studita.domain.repository

import com.example.studita.domain.entity.UserStatisticsData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.enum.UserStatisticsTime

interface UserStatisticsRepository {

    suspend fun getUserStatistics(userIdToken: UserIdTokenData): Pair<Int, List<UserStatisticsData>>

}