package com.example.studita.utils

import com.example.studita.data.cache.authentication.LogInCacheImpl
import com.example.studita.di.CacheModule
import com.example.studita.presentation.activities.DefaultActivity

object PrefsUtils{

    fun isOfflineMode() = CacheModule.sharedPreferences.getBoolean("OFFLINE_MODE", false)

    fun setOfflineMode(enabled: Boolean){
        CacheModule.sharedPreferences.edit().putBoolean("OFFLINE_MODE", enabled).apply()
    }

    fun getUserToken(): String? = CacheModule.sharedPreferences.getString(LogInCacheImpl.TOKEN_PREFS, null)

    fun getUserId(): String? = CacheModule.sharedPreferences.getString(LogInCacheImpl.USER_ID_PREFS, null)

    fun containsOfflineMode() = CacheModule.sharedPreferences.contains("OFFLINE_MODE")

    fun getTheme() = DefaultActivity.Theme.values()[CacheModule.sharedPreferences.getInt("theme", DefaultActivity.Theme.DEFAULT.ordinal)]

    fun setTheme(themeState: DefaultActivity.Theme) = CacheModule.sharedPreferences.edit()?.putInt("theme", themeState.ordinal)?.apply()

}