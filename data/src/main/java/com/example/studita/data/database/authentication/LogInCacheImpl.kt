package com.example.studita.data.database.authentication

import android.content.SharedPreferences
import com.example.studita.data.database.levels.LevelsCache

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