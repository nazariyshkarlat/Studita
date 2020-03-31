package com.example.studita.data.database.user_data

import com.example.studita.data.entity.UserDataEntity

interface UserDataCache {
    fun saveUserDataJson(json: String)
    fun getUserDataJson(): String?
    fun isCached(): Boolean
}