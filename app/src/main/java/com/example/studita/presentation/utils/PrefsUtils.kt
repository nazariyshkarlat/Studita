package com.example.studita.presentation.utils

import com.example.studita.data.database.authentication.LogInCacheImpl
import com.example.studita.di.DiskModule

object PrefsUtils {

    private var userToken: String? = null
    private var userId: String? = null

    fun getUserToken(): String? =
        if(userToken == null) DiskModule.sharedPreferences.getString(LogInCacheImpl.TOKEN_PREFS, null) else userToken

    fun getUserId(): String? =
        if(userId == null)DiskModule.sharedPreferences.getString(LogInCacheImpl.USER_ID_PREFS, null) else userId
}