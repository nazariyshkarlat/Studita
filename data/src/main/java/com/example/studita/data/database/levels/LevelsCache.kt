package com.example.studita.data.database.levels

interface LevelsCache{
    fun saveLevelsJson(json: String)

    fun getLevelsJson(): String?

    fun isCached(): Boolean
}