package com.example.studita.data.cache.levels

import android.content.SharedPreferences

class LevelsCacheImpl(private val sharedPreferences: SharedPreferences) :
    LevelsCache {

    companion object{
        const val LEVELS_PREFS = "levels_cache"
    }

    override fun saveLevelsJson(isLoggedIn: Boolean, json: String) {
        sharedPreferences.edit().putString("${if(isLoggedIn) "LOGGED" else "UN_LOGGED"}_$LEVELS_PREFS", json).apply()
    }

    override fun getLevelsJson(isLoggedIn: Boolean): String? = sharedPreferences.getString("${if(isLoggedIn) "LOGGED" else "UN_LOGGED"}_$LEVELS_PREFS", null) ?: null

    override fun isCached(isLoggedIn: Boolean): Boolean {
        val value = sharedPreferences.getString("${if(isLoggedIn) "LOGGED" else "UN_LOGGED"}_$LEVELS_PREFS", null)
        return value?.isNotEmpty() ?: false
    }

}