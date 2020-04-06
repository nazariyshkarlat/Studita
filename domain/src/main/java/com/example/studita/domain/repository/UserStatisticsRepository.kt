package com.example.studita.domain.repository

import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserStatisticsData
import com.example.studita.domain.enum.UserStatisticsTime

interface UserStatisticsRepository {

    suspend fun getUserStatistics(userId: String, userToken: String, time: UserStatisticsTime): Pair<Int, UserStatisticsData>

}