package com.example.studita.data.cache.levels

interface LevelsCache{
    fun saveLevelsJson(json: String)

    fun getLevelsJson(): String?

    fun isCached(): Boolean
}