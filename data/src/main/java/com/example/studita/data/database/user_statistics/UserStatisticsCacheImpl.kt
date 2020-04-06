package com.example.studita.data.database.user_statistics

import android.content.SharedPreferences

class UserStatisticsCacheImpl(private val sharedPreferences: SharedPreferences) : UserStatisticsCache {

    companion object{
        const val USER_DATA_PREFS = "user_statistics_cache"
    }

    override fun saveUserStatisticsJson(json: String) {
        sharedPreferences.edit().putString(USER_DATA_PREFS, json).apply()
    }

    override fun getUserStatisticsJson(): String? = sharedPreferences.getString(USER_DATA_PREFS, null) ?: null

    override fun isCached(): Boolean {
        val value = sharedPreferences.getString(USER_DATA_PREFS, null)
        return value?.isNotEmpty() ?: false
    }

}