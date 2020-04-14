package com.example.studita.data.cache.authentication

interface LogInCache {
    fun saveUserAuthenticationInfo(userId: String, token: String)
}