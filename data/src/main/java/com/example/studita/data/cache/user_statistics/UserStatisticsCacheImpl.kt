package com.example.studita.data.cache.user_statistics

import android.content.SharedPreferences
import com.example.studita.data.cache.chapter.ChapterCacheImpl
import com.example.studita.domain.enum.UserStatisticsTime
import com.example.studita.domain.enum.timeToString

class UserStatisticsCacheImpl(private val sharedPreferences: SharedPreferences) : UserStatisticsCache {

    companion object{
        const val USER_STATISTICS_PREFS = "user_statistics_cache"
    }

    override fun saveUserStatisticsJson(userStatisticsTime: UserStatisticsTime, json: String) {
        sharedPreferences.edit().putString("${USER_STATISTICS_PREFS}_${userStatisticsTime.timeToString()}", json).apply()
    }

    override fun getUserStatisticsJson(userStatisticsTime: UserStatisticsTime): String? = sharedPreferences.getString("${USER_STATISTICS_PREFS}_${userStatisticsTime.timeToString()}", null) ?: null

    override fun isCached(userStatisticsTime: UserStatisticsTime): Boolean {
        val value = sharedPreferences.getString("${USER_STATISTICS_PREFS}_${userStatisticsTime.timeToString()}", null)
        return value?.isNotEmpty() ?: false
    }

}