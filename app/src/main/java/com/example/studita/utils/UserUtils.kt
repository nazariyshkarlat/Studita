package com.example.studita.utils

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.studita.App
import com.example.studita.di.data.UserDataModule
import com.example.studita.domain.entity.UserData
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.users.UsersInteractor
import com.example.studita.presentation.activities.MainActivity
import com.example.studita.presentation.view_model.LiveEvent
import com.example.studita.presentation.view_model.SingleLiveEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object UserUtils {

    val userDataLiveData = MutableLiveData<UserDataData>()
    val userDataEventsLiveData = LiveEvent<UserDataData>().apply {
        addSource(userDataLiveData) {
            this.value = it
        }
    }
    val userData: UserDataData
    get(){
        return userDataLiveData.value as UserDataData
    }
    val isMyFriendLiveData = LiveEvent<UsersInteractor.FriendActionState>()
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

    fun userDataNotNull() = userDataLiveData.value != null

    fun clearUserIdToken(){
        userID = null
        userToken = null
        PrefsUtils.clearUserIdToken()
    }

    fun deviceSignOut(userDataLifecycleOwner: LifecycleOwner){
        runBlocking {
            GlobalScope.launch { UserDataModule.getUserDataInteractorImpl().deleteUserData() }
            userDataLiveData.removeObservers(userDataLifecycleOwner)
            clearUserIdToken()
            userDataLiveData.value = null
            App.getUserData()
        }
    }

    fun isLoggedIn() = getUserIDTokenData() != null
}