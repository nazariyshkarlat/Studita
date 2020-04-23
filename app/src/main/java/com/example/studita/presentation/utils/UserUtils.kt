package com.example.studita.presentation.utils

import com.example.studita.data.cache.authentication.LogInCacheImpl
import com.example.studita.di.CacheModule
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserIdTokenData
import java.util.*

object UserUtils {

    private var userToken: String? = null
    private var userId: String? = null
    lateinit var userData: UserDataData
    var oldUserData: UserDataData? = null

    private fun getUserToken(): String? =
        if(userToken == null) CacheModule.sharedPreferences.getString(LogInCacheImpl.TOKEN_PREFS, null) else userToken

    private fun getUserId(): String? =
        if(userId == null)CacheModule.sharedPreferences.getString(LogInCacheImpl.USER_ID_PREFS, null) else userId

    fun getUserTokenIdData(): UserIdTokenData? = getUserId()?.let { getUserToken()?.let { it1 ->
        UserIdTokenData(it,
            it1
        )
    } }

    fun isLoggedIn() = getUserTokenIdData() != null
}