package com.example.studita.data.repository.datasource.user_statistics

import com.example.studita.data.database.user_statistics.UserStatisticsCache
import com.example.studita.domain.enum.UserStatisticsTime


class DiskUserStatisticsJsonDataStore(private val userStatisticsCache: UserStatisticsCache) : UserStatisticsJsonDataStore {
    override suspend fun getUserStatisticsJson(
        userId: String,
        userToken: String,
        userStatisticsTime: UserStatisticsTime
    ): Pair<Int, String?> = 200 to userStatisticsCache.getUserStatisticsJson()
}