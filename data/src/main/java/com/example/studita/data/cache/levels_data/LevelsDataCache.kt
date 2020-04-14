package com.example.studita.data.cache.levels_data

interface LevelsDataCache{
    fun saveLevelsDataJson(json: String)
    fun getLevelsDataJson(): String?
    fun isCached(): Boolean
}