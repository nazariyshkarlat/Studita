package com.example.studita.utils

import androidx.lifecycle.MutableLiveData
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.presentation.view_model.SingleLiveEvent

object UserUtils {

    val userDataLiveData = MutableLiveData<UserDataData>()
    private var userToken: String? = null
    private var userID: String? = null
    lateinit var userData: UserDataData
    var oldUserData: UserDataData? = null

    private fun getUserToken(): String? =
        if(userToken == null) PrefsUtils.getUserToken() else userToken

    private fun getUserID(): String? =
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