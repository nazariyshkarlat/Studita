package com.example.studita.data.cache.levels_data

import android.content.SharedPreferences

class LevelsDataCacheImpl(private val sharedPreferences: SharedPreferences) : LevelsDataCache{

    companion object{
        const val LEVELS_DATA_PREFS = "levels_data_cache"
    }

    override fun saveLevelsDataJson(json: String) {
        sharedPreferences.edit().putString(LEVELS_DATA_PREFS, json).apply()
    }

    override fun getLevelsDataJson(): String? = sharedPreferences.getString(LEVELS_DATA_PREFS, null) ?: null

    override fun isCached(): Boolean {
        val value = sharedPreferences.getString(LEVELS_DATA_PREFS, null)
        return value?.isNotEmpty() ?: false
    }

}