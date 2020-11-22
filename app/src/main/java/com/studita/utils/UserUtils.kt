package com.studita.utils

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.studita.App
import com.studita.App.Companion.authenticate
import com.studita.App.Companion.clearUserLiveData
import com.studita.authenticator.AccountAuthenticator
import com.studita.di.data.AuthorizationModule
import com.studita.di.data.CompleteExercisesModule
import com.studita.di.data.UserDataModule
import com.studita.domain.entity.SignOutRequestData
import com.studita.domain.entity.UserDataData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.interactor.users.UsersInteractor
import com.studita.presentation.view_model.LiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object UserUtils {

    var userDataLiveData = MutableLiveData<UserDataData>()

    var localUserDataLiveData = MediatorLiveData<UserDataData>().apply {
        addSource(userDataLiveData) {
            if(it != null)
                this.value = it
        }
    }

    var userDataEventsLiveData = LiveEvent<UserDataData>().apply {
        addSource(userDataLiveData) {
            if(it != null)
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
            clearUserLiveData()
        }
    }

    fun signOut(application: Application){

        val userIdTokenData = UserUtils.getUserIDTokenData()!!

        GlobalScope.launch(Dispatchers.Main) {
            AuthorizationModule.getAuthorizationInteractorImpl().signOut(
                SignOutRequestData(
                    userIdTokenData,
                    DeviceUtils.getDeviceId(application)
                )
            )
        }

        deviceSignOut()
        authenticate(null, false)
    }

    fun isLoggedIn() = getUserIDTokenData() != null

    fun <T> MutableLiveData<T>.observeNoNull(lifecycleOwner: LifecycleOwner, observer: Observer<T>){
        observe(lifecycleOwner, Observer {
            if(it != null){
                observer.onChanged(it)
            }
        })
    }
}