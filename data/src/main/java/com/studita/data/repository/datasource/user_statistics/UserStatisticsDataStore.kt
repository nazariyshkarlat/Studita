package com.studita.data.repository.datasource.user_statistics

import com.studita.data.entity.UserStatisticsEntity

interface UserStatisticsDataStore {
    suspend fun getUserStatisticsEntity(userId: Int): Pair<Int, List<UserStatisticsEntity>>
}