package com.example.studita.data.cache.levels

interface LevelsCache {
    fun saveLevelsJson(isLoggedIn: Boolean, json: String)

    fun getLevelsJson(isLoggedIn: Boolean): String?

    fun isCached(isLoggedIn: Boolean): Boolean
}