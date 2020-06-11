package com.example.studita.utils

import androidx.lifecycle.MutableLiveData
import com.example.studita.domain.entity.UserData
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.presentation.view_model.LiveEvent

object UserUtils {

    val userDataLiveData = MutableLiveData<UserDataData>()
    val userData: UserDataData
    get(){
        return userDataLiveData.value as UserDataData
    }
    val isMyFriendLiveData = LiveEvent<UserData>()
    private var userToken: String? = null
    private var userID: Int? = null

    private fun getUserToken(): String? =
        if(userToken == null) PrefsUtils.getUserToken() else userToken

    private fun getUserID(): Int? =
        if(userID == null) PrefsUtils.getUserId() else userID

    fun getUserIDTokenData(): UserIdTokenData? = getUserID()?.let { getUserToken()?.let { it1 ->
        UserIdTokenData(it,
            it1
        )
    } }

    fun clearUserIdToken(){
        userID = null
        userToken = null
    }

    fun isLoggedIn() = getUserIDTokenData() != null
}