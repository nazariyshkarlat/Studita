package com.example.studita.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.studita.App
import com.example.studita.data.database.StuditaDatabase
import com.example.studita.di.DatabaseModule
import com.example.studita.di.data.CompleteExercisesModule
import com.example.studita.di.data.UserDataModule
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.users.UsersInteractor
import com.example.studita.presentation.view_model.LiveEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object UserUtils {

    var userDataLiveData = MutableLiveData<UserDataData>()
    val userDataEventsLiveData = LiveEvent<UserDataData>().apply {
        addSource(userDataLiveData) {
            this.value = it
        }
    }
    val userData: UserDataData
        get() {
            return userDataLiveData.value as UserDataData
        }
    val isMyFriendLiveData = LiveEvent<UsersInteractor.FriendActionState>()
    private var userToken: String? = null
    private var userID: Int? = null

    private fun getUserToken(): String? =
        if (userToken == null) PrefsUtils.getUserToken() else userToken

    private fun getUserID(): Int? =
        if (userID == null) PrefsUtils.getUserId() else userID

    fun getUserIDTokenData(): UserIdTokenData? = getUserID()?.let {
        getUserToken()?.let { it1 ->
            UserIdTokenData(
                it,
                it1
            )
        }
    }

    fun userDataNotNull() = userDataLiveData.value != null

    fun userDataIsNull() = userDataLiveData.value == null

    private fun clearUserIdToken() {
        userID = null
        userToken = null
        PrefsUtils.clearUserIdToken()
    }

    fun deviceSignOut() {
        runBlocking {
            GlobalScope.launch {
                UserDataModule.getUserDataInteractorImpl().deleteUserData()
                CompleteExercisesModule.getCompleteExercisesInteractorImpl().clearLocalCompletedExercises()
            }
            clearUserIdToken()
            userDataEventsLiveData.removeSource(userDataLiveData)
            userDataLiveData = MutableLiveData<UserDataData>()
            userDataEventsLiveData.addSource(userDataLiveData) {
                 userDataEventsLiveData.value = it
            }
        }
    }

    fun isLoggedIn() = getUserIDTokenData() != null
}