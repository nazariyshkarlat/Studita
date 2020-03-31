package com.example.studita.presentation.utils

import com.example.studita.data.database.authentication.LogInCache
import com.example.studita.data.database.authentication.LogInCacheImpl
import com.example.studita.di.DiskModule

object PrefsUtils {
    fun getUserToken(): String? =
        DiskModule.sharedPreferences.getString(LogInCacheImpl.TOKEN_PREFS, null)

    fun getUserId(): String? =
        DiskModule.sharedPreferences.getString(LogInCacheImpl.USER_ID_PREFS, null)
}