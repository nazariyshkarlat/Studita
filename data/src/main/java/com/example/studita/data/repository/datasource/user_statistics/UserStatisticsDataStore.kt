package com.example.studita.data.repository.datasource.user_statistics

import com.example.studita.data.entity.UserIdToken
import com.example.studita.data.entity.UserStatisticsEntity
import com.example.studita.domain.enum.UserStatisticsTime

interface UserStatisticsDataStore {
    suspend fun getUserStatisticsEntity(userId: Int): Pair<Int, List<UserStatisticsEntity>>
}