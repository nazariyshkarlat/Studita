package com.example.studita.data.cache.user_data

import android.content.SharedPreferences

class UserDataCacheImpl(private val sharedPreferences: SharedPreferences) : UserDataCache{

    companion object{
        const val USER_DATA_PREFS = "user_data_cache"
    }

    override fun saveUserDataJson(json: String) {
        sharedPreferences.edit().putString(USER_DATA_PREFS, json).apply()
    }

    override fun getUserDataJson(): String? = sharedPreferences.getString(USER_DATA_PREFS, null) ?: null

    override fun isCached(): Boolean {
        val value = sharedPreferences.getString(USER_DATA_PREFS, null)
        return value?.isNotEmpty() ?: false
    }

}