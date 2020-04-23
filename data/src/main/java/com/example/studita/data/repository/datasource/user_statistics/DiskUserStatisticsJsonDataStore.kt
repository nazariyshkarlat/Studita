package com.example.studita.data.repository.datasource.user_statistics

import com.example.studita.data.cache.user_statistics.UserStatisticsCache
import com.example.studita.data.entity.UserIdToken
import com.example.studita.domain.enum.UserStatisticsTime
import com.example.studita.domain.enum.timeToString


class DiskUserStatisticsJsonDataStore(private val userStatisticsCache: UserStatisticsCache) : UserStatisticsJsonDataStore {
    override suspend fun getUserStatisticsJson(
        userIdToken: UserIdToken
    ): Pair<Int, String?> = 200 to userStatisticsCache.getUserStatisticsJson()
}