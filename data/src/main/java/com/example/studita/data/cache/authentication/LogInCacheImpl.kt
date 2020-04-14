package com.example.studita.data.cache.authentication

import android.content.SharedPreferences

class LogInCacheImpl(private val sharedPreferences: SharedPreferences) :
    LogInCache {

    companion object{
        const val USER_ID_PREFS = "user_id"
        const val TOKEN_PREFS = "token"
    }

    override fun saveUserAuthenticationInfo(userId: String, token: String) {
        sharedPreferences.edit().putString(USER_ID_PREFS, userId).putString(TOKEN_PREFS, token).apply()
    }

}