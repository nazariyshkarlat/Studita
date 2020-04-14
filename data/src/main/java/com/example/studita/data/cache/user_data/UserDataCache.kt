package com.example.studita.data.cache.user_data

interface UserDataCache {
    fun saveUserDataJson(json: String)
    fun getUserDataJson(): String?
    fun isCached(): Boolean
}