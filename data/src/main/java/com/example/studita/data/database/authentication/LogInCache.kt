package com.example.studita.data.database.authentication

interface LogInCache {
    fun saveUserAuthenticationInfo(userId: String, token: String)
}