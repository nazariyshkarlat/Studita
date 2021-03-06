package com.studita

import android.app.Application
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.*
import com.studita.di.*
import com.studita.di.data.*
import com.studita.domain.entity.UserDataData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.interactor.CheckTokenIsCorrectStatus
import com.studita.domain.interactor.UserDataStatus
import com.studita.domain.interactor.authorization.AuthorizationInteractor
import com.studita.domain.interactor.complete_chapter_part.CompleteExercisesInteractor
import com.studita.domain.interactor.user_data.UserDataInteractor
import com.studita.notifications.local.LocalNotificationsService
import com.studita.notifications.local.StartUpReceiver.Companion.scheduleLocalNotifications
import com.studita.presentation.view_model.LiveEvent
import com.studita.presentation.view_model.SingleLiveEvent
import com.studita.utils.*
import com.studita.utils.UserUtils.localUserDataLiveData
import com.studita.utils.UserUtils.userDataLiveData
import kotlinx.coroutines.*
import okhttp3.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import java.util.*


class App : Application(), LifecycleObserver {


    companion object {
        var userDataDeferred: CompletableDeferred<UserDataStatus> = CompletableDeferred()
        val authenticationState = MutableLiveData<Pair<CheckTokenIsCorrectStatus, Boolean>>()
        var authenticationJob: Job? = null
        var userDataJob: Job? = null

        var offlineModeChangeEvent = LiveEvent<Boolean>()
        var recreateAppEvent = LiveEvent<Unit>()

        fun clearUserLiveData() {
            UserUtils.userDataEventsLiveData.removeSource(UserUtils.userDataLiveData)
            UserUtils.localUserDataLiveData.removeSource(UserUtils.userDataLiveData)
            UserUtils.userDataLiveData = MutableLiveData<UserDataData>()

            UserUtils.userDataEventsLiveData = LiveEvent<UserDataData>().apply {
                addSource(userDataLiveData) {
                    if (it != null)
                        UserUtils.userDataEventsLiveData.value = it
                }
            }
            UserUtils.localUserDataLiveData = MediatorLiveData<UserDataData>().apply {
                addSource(userDataLiveData) {
                    if (it != null)
                        localUserDataLiveData.value = it
                }
            }
        }

        fun initUserData(userDataData: UserDataData) {
            UserUtils.userDataLiveData.value = userDataData

            if (!localUserDataLiveData.hasActiveObservers())
                UserUtils.localUserDataLiveData.value = userDataData

            if ((TimeUtils.getCalendarDayCount(
                    userDataData.streakDatetime,
                    Date()
                ) > 1F) && (userDataData.streakDays != 0)
            ) {
                userDataData.streakDays = 0
                userDataData.todayCompletedExercises = 0
            }

            GlobalScope.launch(Dispatchers.Main) {
                GlobalContext.get().get<UserDataInteractor>().saveUserData(userDataData)
            }
        }

        fun getUserData() {

            userDataJob = GlobalScope.launchExt(userDataJob) {
                val userDataStatus = GlobalContext.get().get<UserDataInteractor>()
                    .getUserData(
                        PrefsUtils.getUserId(),
                        PrefsUtils.isOfflineModeEnabled(),
                        true
                    )


                when (userDataStatus) {
                    is UserDataStatus.Success -> {
                        initUserData(userDataStatus.result)
                    }
                }
                userDataDeferred.complete(userDataStatus)
            }
        }

        fun initLocalUserData() {
            GlobalScope.launch(Dispatchers.Main) {
                val userDataStatus = GlobalContext.get().get<UserDataInteractor>()
                    .getUserData(
                        PrefsUtils.getUserId(),
                        getFromLocalStorage = true,
                        isMyUserData = true
                    )
                if (userDataStatus is UserDataStatus.Success) {
                    UserUtils.localUserDataLiveData.value = userDataStatus.result
                }
            }
        }

        fun authenticate(userIdTokenData: UserIdTokenData?, isOfflineModeChanged: Boolean) {

            if (userDataDeferred.isCompleted && UserUtils.isLoggedIn())
                userDataDeferred = CompletableDeferred()

            if (isOfflineModeChanged && !PrefsUtils.isOfflineModeEnabled())
                UserUtils.userDataLiveData.value = null

            if (isOfflineModeChanged) {
                offlineModeChangeEvent.value = PrefsUtils.isOfflineModeEnabled()
            }
            authenticationState.value = CheckTokenIsCorrectStatus.Waiting to isOfflineModeChanged

            authenticationJob = GlobalScope.launchExt(
                authenticationJob
            ) {
                if (PrefsUtils.isOfflineModeEnabled() || !UserUtils.isLoggedIn()) {
                    if (UserUtils.userDataIsNull())
                        getUserData()
                    else
                        userDataDeferred.complete(UserDataStatus.Success(UserUtils.userData))
                    authenticationState.value =
                        CheckTokenIsCorrectStatus.Correct to isOfflineModeChanged
                } else {
                    val tokenIsCorrectStatus = GlobalContext.get().get<AuthorizationInteractor>()
                        .checkTokenIsCorrect(userIdTokenData!!)

                    authenticationState.value = tokenIsCorrectStatus to isOfflineModeChanged

                    when (tokenIsCorrectStatus) {
                        is CheckTokenIsCorrectStatus.Correct -> {
                            if (!PrefsUtils.isOfflineModeEnabled())
                                GlobalContext.get().get<CompleteExercisesInteractor>()
                                    .syncCompleteLocalExercises(
                                        UserUtils.getUserIDTokenData()!!
                                    )
                            getUserData()
                        }
                        is CheckTokenIsCorrectStatus.NoConnection -> {
                            userDataDeferred.complete(UserDataStatus.NoConnection)
                        }
                        is CheckTokenIsCorrectStatus.ServiceUnavailable -> {
                            userDataDeferred.complete(UserDataStatus.ServiceUnavailable)
                        }
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        DI.initialize(this)
        initDefPrefs()
        initLocalUserData()

        if (PrefsUtils.offlineDataIsCached())
            authenticate(
                UserUtils.getUserIDTokenData(),
                false
            )

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        scheduleLocalNotifications(this)
    }


    private fun initDefPrefs() {
        if (!PrefsUtils.containsOfflineMode())
            PrefsUtils.setOfflineMode(false)
        if (!PrefsUtils.containsNotificationsMode())
            PrefsUtils.setNotificationsMode(true)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {

            if (!PrefsUtils.containsNightThemeOnPhoneIsEnabled()) {
                PrefsUtils.setTheme(
                    ThemeUtils.Theme.DEFAULT, ThemeUtils.nightModeApiAbove28Enabled(
                        resources
                    )
                )
            }

        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForeground() {
        LocalNotificationsService.APP_IS_IN_FOREGROUND.set(true)

        PrefsUtils.getLocalNotificationsIds().forEach {
            NotificationManagerCompat.from(this).cancel(it)
        }
        PrefsUtils.clearLocalNotificationsIds()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackground() {
        LocalNotificationsService.APP_IS_IN_FOREGROUND.set(false)
    }

}