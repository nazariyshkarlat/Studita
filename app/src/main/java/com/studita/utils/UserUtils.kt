package com.studita.utils

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.studita.App.Companion.authenticate
import com.studita.App.Companion.clearUserLiveData
import com.studita.domain.entity.SignOutRequestData
import com.studita.domain.entity.UserDataData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.interactor.authorization.AuthorizationInteractor
import com.studita.domain.interactor.complete_chapter_part.CompleteExercisesInteractor
import com.studita.domain.interactor.user_data.UserDataInteractor
import com.studita.domain.interactor.users.UsersInteractor
import com.studita.presentation.view_model.LiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext
import java.util.*

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
                GlobalContext.get().get<UserDataInteractor>().deleteUserData()
                GlobalContext.get().get<CompleteExercisesInteractor>().clearLocalCompletedExercises()
            }
            clearUserIdToken()
            clearUserLiveData()
        }
    }

    fun signOut(application: Application){

        val userIdTokenData = UserUtils.getUserIDTokenData()!!

        GlobalScope.launch(Dispatchers.Main) {
            GlobalContext.get().get<AuthorizationInteractor>().signOut(
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

    fun streakActivated(streakDate: Date) =
        TimeUtils.getCalendarDayCount(Date(), streakDate) == 0L
}