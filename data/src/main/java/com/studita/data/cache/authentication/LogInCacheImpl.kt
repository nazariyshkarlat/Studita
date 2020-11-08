package com.studita.data.cache.authentication

import android.content.SharedPreferences

class LogInCacheImpl(private val sharedPreferences: SharedPreferences) :
    LogInCache {

    companion object {
        const val USER_ID_PREFS = "user_id"
        const val TOKEN_PREFS = "token"
    }

    override fun saveUserAuthenticationInfo(userId: Int, token: String) {
        sharedPreferences.edit().putInt(USER_ID_PREFS, userId).putString(TOKEN_PREFS, token).apply()
    }

    override fun clearUserAuthenticationInfo() {
        sharedPreferences.edit().remove(USER_ID_PREFS).remove(TOKEN_PREFS).apply()
    }

}