package com.example.studita.data.cache.user_statistics

import com.example.studita.domain.enum.UserStatisticsTime

interface UserStatisticsCache {
    fun saveUserStatisticsJson(userStatisticsTime: UserStatisticsTime, json: String)
    fun getUserStatisticsJson(userStatisticsTime: UserStatisticsTime): String?
    fun isCached(userStatisticsTime: UserStatisticsTime): Boolean
}