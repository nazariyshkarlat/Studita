package com.studita

import android.app.Application
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.MutableLiveData
import com.studita.di.DI
import com.studita.di.data.AuthorizationModule
import com.studita.di.data.CompleteExercisesModule
import com.studita.di.data.UserDataModule
import com.studita.domain.entity.UserDataData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.interactor.CheckTokenIsCorrectStatus
import com.studita.domain.interactor.UserDataStatus
import com.studita.presentation.view_model.SingleLiveEvent
import com.studita.utils.*
import com.shakebugs.shake.Shake
import kotlinx.coroutines.*
import java.util.*


class App : Application() {

    companion object {
        var userDataDeferred: CompletableDeferred<UserDataStatus> = CompletableDeferred()
        val authenticationState = MutableLiveData<Pair<CheckTokenIsCorrectStatus, Boolean>>()
        var authenticationJob: Job? = null

        val offlineModeChangeEvent = SingleLiveEvent<Boolean>()
        var recreateAppEvent = SingleLiveEvent<Unit>()

        fun clearUserLiveData(){
            UserUtils.userDataEventsLiveData.removeSource(UserUtils.userDataLiveData)
            UserUtils.localUserDataLiveData.removeSource(UserUtils.userDataLiveData)
            UserUtils.userDataLiveData = MutableLiveData<UserDataData>()
            UserUtils.userDataEventsLiveData.addSource(UserUtils.userDataLiveData) {
                if(it != null)
                    UserUtils.userDataEventsLiveData.value = it
            }
            UserUtils.localUserDataLiveData.addSource(UserUtils.userDataLiveData) {
                if(it != null)
                    UserUtils.localUserDataLiveData.value = it
            }
        }

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

        fun getUserData() {

            GlobalScope.launch(Dispatchers.Main) {
                val userDataStatus = UserDataModule.getUserDataInteractorImpl()
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

        fun initLocalUserData(){
            GlobalScope.launch(Dispatchers.Main) {
                val userDataStatus = UserDataModule.getUserDataInteractorImpl()
                    .getUserData(
                        PrefsUtils.getUserId(),
                        getFromLocalStorage = true,
                        isMyUserData = true
                    )
                if(userDataStatus is UserDataStatus.Success) {
                    UserUtils.localUserDataLiveData.value = userDataStatus.result
                }
            }
        }

        fun authenticate(userIdTokenData: UserIdTokenData?, isOfflineModeChanged: Boolean) {

            if(userDataDeferred.isCompleted)
                userDataDeferred = CompletableDeferred()

            if(isOfflineModeChanged && !PrefsUtils.isOfflineModeEnabled())
                UserUtils.userDataLiveData.value = null

            if(isOfflineModeChanged){
                offlineModeChangeEvent.value = PrefsUtils.isOfflineModeEnabled()
            }
            authenticationState.value =CheckTokenIsCorrectStatus.Waiting to isOfflineModeChanged

            authenticationJob = GlobalScope.launchExt(
                authenticationJob
            ) {
                if (PrefsUtils.isOfflineModeEnabled() || !UserUtils.isLoggedIn()) {
                    if(UserUtils.userDataIsNull())
                        getUserData()
                    authenticationState.value =CheckTokenIsCorrectStatus.Correct to isOfflineModeChanged
                } else {
                    val tokenIsCorrectStatus = AuthorizationModule.getAuthorizationInteractorImpl()
                        .checkTokenIsCorrect(userIdTokenData!!)

                    authenticationState.value =tokenIsCorrectStatus to isOfflineModeChanged
                    if(tokenIsCorrectStatus == CheckTokenIsCorrectStatus.Correct) {
                        CompleteExercisesModule.getCompleteExercisesInteractorImpl().syncCompleteLocalExercises(UserUtils.getUserIDTokenData()!!)
                        getUserData()
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

        if(PrefsUtils.offlineDataIsCached())
            authenticate(
                UserUtils.getUserIDTokenData(),
                false
            )

        initShaker()
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

    private fun initShaker(){
        Shake.getReportConfiguration().isAutoVideoRecording = true
        Shake.getReportConfiguration().autoVideoRecordingClipDuration = 30
        Shake.start(this)
    }

}