package com.studita.utils

import android.content.SharedPreferences
import com.studita.data.cache.authentication.LogInCacheImpl
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.context.GlobalContext

object PrefsUtils {

    fun isOfflineModeEnabled() = GlobalContext.get().get<SharedPreferences>().getBoolean("OFFLINE_MODE", false)

    fun notificationsAreEnabled() =
        GlobalContext.get().get<SharedPreferences>().getBoolean("NOTIFICATIONS_MODE", true)

    fun setOfflineMode(enabled: Boolean) {
        GlobalContext.get().get<SharedPreferences>().edit().putBoolean("OFFLINE_MODE", enabled).apply()
    }

    fun getLocalNotificationsIds(): ArrayList<Int>{
        return GlobalContext.get().get<SharedPreferences>().getString("LOCAL_NOTIFICATIONS_IDS", "[]")?.let {
            Json.decodeFromString<ArrayList<Int>>(it)
        } ?: ArrayList()
    }

    fun clearLocalNotificationsIds(){
        GlobalContext.get().get<SharedPreferences>().edit().remove("LOCAL_NOTIFICATIONS_IDS").apply()
    }

    fun setLocalNotificationId(id: Int){
        return GlobalContext.get().get<SharedPreferences>().edit().putString("LOCAL_NOTIFICATIONS_IDS", Json.encodeToString(getLocalNotificationsIds().apply {
            add(id)
        })).apply()
    }


    fun makeCompletedChapterDialogWasNotShown(exercisesCount: Int, chapterName: String) {
        GlobalContext.get().get<SharedPreferences>().edit().putString("COMPLETED_CHAPTER_DIALOG", Json.encodeToString(
            mapOf("EXERCISES_COUNT" to exercisesCount, "CHAPTER_NAME" to chapterName))).apply()
    }

    fun makeCompletedChapterDialogWasShown(){
        GlobalContext.get().get<SharedPreferences>().edit().remove("COMPLETED_CHAPTER_DIALOG").apply()
    }

    fun isCompletedChapterDialogWasNotShown() = GlobalContext.get().get<SharedPreferences>().contains("COMPLETED_CHAPTER_DIALOG")

    fun getCompletedChapterDialogData() : Pair<Int, String>? {
        return GlobalContext.get().get<SharedPreferences>().getString("COMPLETED_CHAPTER_DIALOG", null)?.let {
            with(Json.decodeFromString<Map<String, Any>>(it)) {
                (this["EXERCISES_COUNT"] as Double).toInt() to this["CHAPTER_NAME"] as String
            }
        }
    }

    fun setNotificationsMode(enabled: Boolean) {
        GlobalContext.get().get<SharedPreferences>().edit().putBoolean("NOTIFICATIONS_MODE", enabled).apply()
    }

    fun getUserToken(): String? =
        GlobalContext.get().get<SharedPreferences>().getString(LogInCacheImpl.TOKEN_PREFS, null)

    fun clearUserIdToken() {
        GlobalContext.get().get<SharedPreferences>().edit().remove(LogInCacheImpl.TOKEN_PREFS)
            .remove(LogInCacheImpl.USER_ID_PREFS).apply()
    }

    fun saveHomeLayoutCollapsedLevel(levelNumber: Int){
        GlobalContext.get().get<SharedPreferences>().edit().putString("COLLAPSED_LEVELS",
            Json.encodeToString(
                Json.decodeFromString<MutableMap<String, Boolean>>(
                    GlobalContext.get().get<SharedPreferences>()
                        .getString("COLLAPSED_LEVELS", "{}")!!
                )
                    .apply {
                        put("$levelNumber", true)
                    }
            )
        ).apply()
    }

    fun getHomeLayoutCollapsedLevels() =
        Json.decodeFromString<MutableMap<String, Boolean>>(
            GlobalContext.get().get<SharedPreferences>().getString("COLLAPSED_LEVELS", "{}")!!
        ).map{ it.key.toInt() to it.value }

    fun removeHomeLayoutCollapsedLevel(levelNumber: Int){
            GlobalContext.get().get<SharedPreferences>().edit().putString("COLLAPSED_LEVELS",
                Json.encodeToString(
                    Json.decodeFromString<MutableMap<String, Boolean>>(
                        GlobalContext.get().get<SharedPreferences>().getString("COLLAPSED_LEVELS", "{}")!!)
                        .apply {
                            remove("$levelNumber")
                        }
                )
            ).apply()
    }

    fun getUserId(): Int? {
        val userId = GlobalContext.get().get<SharedPreferences>().getInt(LogInCacheImpl.USER_ID_PREFS, 0)
        return if (userId == 0) null else userId
    }

    fun containsOfflineMode() = GlobalContext.get().get<SharedPreferences>().contains("OFFLINE_MODE")

    fun containsNotificationsMode() = GlobalContext.get().get<SharedPreferences>().contains("NOTIFICATIONS_MODE")

    fun containsNightThemeOnPhoneIsEnabled() = GlobalContext.get().get<SharedPreferences>().contains("NIGHT_MODE_WHEN_SAVE_ENABLED")

    fun getTheme() = ThemeUtils.Theme.values()[GlobalContext.get().get<SharedPreferences>().getInt(
        "theme",
        ThemeUtils.Theme.DEFAULT.ordinal
    )]

    fun nightModeWhenSaveWasEnabled() = GlobalContext.get().get<SharedPreferences>().getBoolean("NIGHT_MODE_WHEN_SAVE_ENABLED", true)

    fun offlineDataIsCached() = GlobalContext.get().get<SharedPreferences>().getBoolean("OFFLINE_DATA_IS_CACHED", false)

    fun setOfflineDataIsCached(){
        GlobalContext.get().get<SharedPreferences>().edit()?.putBoolean("OFFLINE_DATA_IS_CACHED", true)?.apply()
    }

    fun setTheme(themeState: ThemeUtils.Theme, nightModeIsEnabled: Boolean) {
        GlobalContext.get().get<SharedPreferences>().edit()?.putInt("theme", themeState.ordinal)?.apply()

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P)
            GlobalContext.get().get<SharedPreferences>().edit()?.putBoolean("NIGHT_MODE_WHEN_SAVE_ENABLED", nightModeIsEnabled)?.apply()
    }
}