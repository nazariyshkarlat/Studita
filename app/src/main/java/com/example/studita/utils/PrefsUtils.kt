package com.example.studita.utils

import com.example.studita.data.cache.authentication.LogInCacheImpl
import com.example.studita.di.CacheModule
import com.example.studita.presentation.activities.DefaultActivity

object PrefsUtils {

    fun isOfflineModeEnabled() = CacheModule.sharedPreferences.getBoolean("OFFLINE_MODE", false)

    fun notificationsAreEnabled() =
        CacheModule.sharedPreferences.getBoolean("NOTIFICATIONS_MODE", true)

    fun setOfflineMode(enabled: Boolean) {
        CacheModule.sharedPreferences.edit().putBoolean("OFFLINE_MODE", enabled).apply()
    }

    fun setNotificationsMode(enabled: Boolean) {
        CacheModule.sharedPreferences.edit().putBoolean("NOTIFICATIONS_MODE", enabled).apply()
    }

    fun getUserToken(): String? =
        CacheModule.sharedPreferences.getString(LogInCacheImpl.TOKEN_PREFS, null)

    fun clearUserIdToken() {
        CacheModule.sharedPreferences.edit().remove(LogInCacheImpl.TOKEN_PREFS)
            .remove(LogInCacheImpl.USER_ID_PREFS).apply()
    }

    fun getUserId(): Int? {
        val userId = CacheModule.sharedPreferences.getInt(LogInCacheImpl.USER_ID_PREFS, 0)
        return if (userId == 0) null else userId
    }

    fun containsOfflineMode() = CacheModule.sharedPreferences.contains("OFFLINE_MODE")

    fun containsNotificationsMode() = CacheModule.sharedPreferences.contains("NOTIFICATIONS_MODE")

    fun getTheme() = DefaultActivity.Theme.values()[CacheModule.sharedPreferences.getInt(
        "theme",
        DefaultActivity.Theme.DEFAULT.ordinal
    )]

    fun setTheme(themeState: DefaultActivity.Theme) =
        CacheModule.sharedPreferences.edit()?.putInt("theme", themeState.ordinal)?.apply()
}