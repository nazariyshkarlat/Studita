package com.example.studita.presentation.utils

import com.example.studita.data.cache.authentication.LogInCacheImpl
import com.example.studita.di.DiskModule
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserTokenIdData
import java.util.*

object UserUtils {

    private var userToken: String? = null
    private var userId: String? = null
    var userData: UserDataData? = UserDataData("", "", "", 1, 0, 0, false, arrayListOf(0,0,0,0), Date())
    var oldUserData: UserDataData? = null

    private fun getUserToken(): String? =
        if(userToken == null) DiskModule.sharedPreferences.getString(LogInCacheImpl.TOKEN_PREFS, null) else userToken

    private fun getUserId(): String? =
        if(userId == null)DiskModule.sharedPreferences.getString(LogInCacheImpl.USER_ID_PREFS, null) else userId

    fun getUserTokenIdData(): UserTokenIdData? = getUserId()?.let { getUserToken()?.let { it1 ->
        UserTokenIdData(it,
            it1
        )
    } }

    fun isLoggedIn() = getUserTokenIdData() != null
}