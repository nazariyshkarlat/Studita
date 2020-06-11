package com.example.studita.data.cache.user_statistics

import android.content.SharedPreferences
import com.example.studita.data.cache.chapter.ChapterCacheImpl
import com.example.studita.domain.enum.UserStatisticsTime
import com.example.studita.domain.enum.timeToString

class UserStatisticsCacheImpl(private val sharedPreferences: SharedPreferences) : UserStatisticsCache {

    companion object{
        const val USER_STATISTICS_PREFS = "user_statistics_cache"
    }

    override fun saveUserStatisticsJson(json: String) {
        sharedPreferences.edit().putString(USER_STATISTICS_PREFS, json).apply()
    }

    override fun getUserStatisticsJson(): String? = sharedPreferences.getString(USER_STATISTICS_PREFS, null)

    override fun isCached(): Boolean {
        val value = sharedPreferences.getString(USER_STATISTICS_PREFS, null)
        return value?.isNotEmpty() ?: false
    }

}