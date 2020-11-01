package com.example.studita.utils

import com.example.studita.data.cache.authentication.LogInCacheImpl
import com.example.studita.di.CacheModule
import com.example.studita.presentation.activities.DefaultActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PrefsUtils {

    fun isOfflineModeEnabled() = CacheModule.sharedPreferences.getBoolean("OFFLINE_MODE", false)

    fun notificationsAreEnabled() =
        CacheModule.sharedPreferences.getBoolean("NOTIFICATIONS_MODE", true)

    fun setOfflineMode(enabled: Boolean) {
        CacheModule.sharedPreferences.edit().putBoolean("OFFLINE_MODE", enabled).apply()
    }

    fun makeCompletedChapterDialogWasNotShown(exercisesCount: Int, chapterName: String) {
        CacheModule.sharedPreferences.edit().putString("COMPLETED_CHAPTER_DIALOG", Gson().toJson(
            mapOf("EXERCISES_COUNT" to exercisesCount, "CHAPTER_NAME" to chapterName))).apply()
    }

    fun makeCompletedChapterDialogWasShown(){
        CacheModule.sharedPreferences.edit().remove("COMPLETED_CHAPTER_DIALOG").apply()
    }

    fun isCompletedChapterDialogWasNotShown() = CacheModule.sharedPreferences.contains("COMPLETED_CHAPTER_DIALOG")

    fun getCompletedChapterDialogData() : Pair<Int, String> {
        return with(Gson().fromJson<Map<String, Any>>(CacheModule.sharedPreferences.getString("COMPLETED_CHAPTER_DIALOG", null), object : TypeToken<Map<String, Any>>() { }.type)){
            (this["EXERCISES_COUNT"] as Double).toInt() to this["CHAPTER_NAME"] as String
        }
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

    fun containsNightThemeOnPhoneIsEnabled() = CacheModule.sharedPreferences.contains("NIGHT_MODE_WHEN_SAVE_ENABLED")

    fun getTheme() = ThemeUtils.Theme.values()[CacheModule.sharedPreferences.getInt(
        "theme",
        ThemeUtils.Theme.DEFAULT.ordinal
    )]

    fun nightModeWhenSaveWasEnabled() = CacheModule.sharedPreferences.getBoolean("NIGHT_MODE_WHEN_SAVE_ENABLED", true)

    fun offlineDataIsCached() = CacheModule.sharedPreferences.getBoolean("OFFLINE_DATA_IS_CACHED", false)

    fun setOfflineDataIsCached(){
        CacheModule.sharedPreferences.edit()?.putBoolean("OFFLINE_DATA_IS_CACHED", true)?.apply()
    }

    fun setTheme(themeState: ThemeUtils.Theme, nightModeIsEnabled: Boolean) {
        CacheModule.sharedPreferences.edit()?.putInt("theme", themeState.ordinal)?.apply()

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P)
            CacheModule.sharedPreferences.edit()?.putBoolean("NIGHT_MODE_WHEN_SAVE_ENABLED", nightModeIsEnabled)?.apply()
    }
}