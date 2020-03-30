package com.example.studita.data.database.levels

interface LevelsCache{
    fun putLevelsJson(json: String)

    fun getLevelsJson(): String?

    fun isCached(): Boolean
}