package com.example.studita.data.database.levels

import android.content.Context
import android.content.SharedPreferences

class LevelsCacheImpl(private val sharedPreferences: SharedPreferences) :
    LevelsCache {

    override fun putLevelsJson(json: String) {
        sharedPreferences.edit().putString("levels_cache", json).apply()
    }

    override fun getLevelsJson(): String? = sharedPreferences.getString("levels_cache", null) ?: null

    override fun isCached(): Boolean {
        val value = sharedPreferences.getString("levels_cache", null)
        return value?.isNotEmpty() ?: false
    }

}