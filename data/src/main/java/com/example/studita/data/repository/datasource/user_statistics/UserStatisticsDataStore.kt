package com.example.studita.data.repository.datasource.user_statistics

import com.example.studita.data.entity.UserStatisticsEntity
import com.example.studita.domain.enum.UserStatisticsTime

interface UserStatisticsDataStore {
    suspend fun getUserStatisticsEntity(userId: String, userToken: String, time: UserStatisticsTime): Pair<Int, UserStatisticsEntity>
}