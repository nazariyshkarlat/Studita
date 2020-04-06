package com.example.studita.data.database.levels

import android.content.Context
import android.content.SharedPreferences

class LevelsCacheImpl(private val sharedPreferences: SharedPreferences) :
    LevelsCache {

    companion object{
        const val LEVELS_PREFS = "levels_cache"
    }

    override fun saveLevelsJson(json: String) {
        sharedPreferences.edit().putString(LEVELS_PREFS, json).apply()
    }

    override fun getLevelsJson(): String? = sharedPreferences.getString(LEVELS_PREFS, null) ?: null

    override fun isCached(): Boolean {
        val value = sharedPreferences.getString(LEVELS_PREFS, null)
        return value?.isNotEmpty() ?: false
    }

}