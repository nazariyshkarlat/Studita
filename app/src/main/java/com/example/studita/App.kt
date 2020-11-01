package com.example.studita

import android.app.Application
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.MutableLiveData
import com.example.studita.di.DI
import com.example.studita.di.data.AuthorizationModule
import com.example.studita.di.data.CompleteExercisesModule
import com.example.studita.di.data.UserDataModule
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.CheckTokenIsCorrectStatus
import com.example.studita.domain.interactor.UserDataStatus
import com.example.studita.utils.*
import kotlinx.coroutines.*
import java.util.*


class App : Application() {

    companion object {
        lateinit var userDataDeferred: Deferred<UserDataStatus>
        val applicationScope = CoroutineScope(Dispatchers.Main)
        val authenticationState = MutableLiveData<Pair<CheckTokenIsCorrectStatus, Boolean>>()
        var authenticationJob: Job? = null

        fun initUserData(userDataData: UserDataData) {
            UserUtils.userDataLiveData.value = userDataData

            if ((TimeUtils.getCalendarDayCount(
                    userDataData.streakDatetime,
                    Date()
                ) > 1F) && (userDataData.streakDays != 0)
            ) {
                userDataData.streakDays = 0
                userDataData.todayCompletedExercises = 0
            }

            GlobalScope.launch(Dispatchers.Main) {
                UserDataModule.getUserDataInteractorImpl().saveUserData(userDataData)
            }
        }

        fun getUserData(offlineOnly: Boolean = false) {
            userDataDeferred = applicationScope.async(Dispatchers.Main) {
                val userDataStatus = UserDataModule.getUserDataInteractorImpl()
                    .getUserData(
                        PrefsUtils.getUserId(),
                        if (offlineOnly) true else PrefsUtils.isOfflineModeEnabled(),
                        true
                    )

                    if (userDataStatus is UserDataStatus.Success) {
                        initUserData(userDataStatus.result)
                    }
                    userDataStatus
            }
        }

        fun authenticate(userIdTokenData: UserIdTokenData?, isOfflineModeChanged: Boolean) {
            authenticationState.value =CheckTokenIsCorrectStatus.Waiting to isOfflineModeChanged
                if (PrefsUtils.isOfflineModeEnabled() || !UserUtils.isLoggedIn()) {
                    if(UserUtils.userDataIsNull())
                        getUserData()
                    authenticationState.value = CheckTokenIsCorrectStatus.Correct to isOfflineModeChanged
                } else {
                    authenticationJob = applicationScope.launchExt(authenticationJob) {
                        authenticationState.value =CheckTokenIsCorrectStatus.Waiting to isOfflineModeChanged
                        val tokenIsCorrectStatus = AuthorizationModule.getAuthorizationInteractorImpl()
                            .checkTokenIsCorrect(userIdTokenData!!)
                        authenticationState.value =tokenIsCorrectStatus to isOfflineModeChanged
                        if(tokenIsCorrectStatus == CheckTokenIsCorrectStatus.Correct) {
                            CompleteExercisesModule.getCompleteExercisesInteractorImpl().syncCompleteLocalExercises(UserUtils.getUserIDTokenData()!!)
                            getUserData()
                            userDataDeferred.await()
                        }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        DI.initialize(this)
        initDefPrefs()

        if(PrefsUtils.offlineDataIsCached())
            authenticate(UserUtils.getUserIDTokenData(), false)

    }


    private fun initDefPrefs() {
        if (!PrefsUtils.containsOfflineMode())
            PrefsUtils.setOfflineMode(false)
        if (!PrefsUtils.containsNotificationsMode())
            PrefsUtils.setNotificationsMode(true)
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P){

            if(!PrefsUtils.containsNightThemeOnPhoneIsEnabled()){
                PrefsUtils.setTheme(
                    ThemeUtils.Theme.DEFAULT, ThemeUtils.nightModeApiAbove28Enabled(
                        resources
                    )
                )
            }

        }

    }

}