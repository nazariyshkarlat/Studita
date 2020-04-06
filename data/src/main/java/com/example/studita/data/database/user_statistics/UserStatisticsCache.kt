package com.example.studita.data.database.user_statistics

interface UserStatisticsCache {
    fun saveUserStatisticsJson(json: String)
    fun getUserStatisticsJson(): String?
    fun isCached(): Boolean
}