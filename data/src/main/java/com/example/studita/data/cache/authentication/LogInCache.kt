package com.example.studita.data.cache.authentication

interface LogInCache {
    fun saveUserAuthenticationInfo(userId: Int, token: String)

    fun clearUserAuthenticationInfo()
}