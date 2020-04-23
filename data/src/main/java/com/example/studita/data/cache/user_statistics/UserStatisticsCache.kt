package com.example.studita.data.cache.user_statistics

import com.example.studita.domain.enum.UserStatisticsTime

interface UserStatisticsCache {
    fun saveUserStatisticsJson(json: String)
    fun getUserStatisticsJson(): String?
    fun isCached(): Boolean
}