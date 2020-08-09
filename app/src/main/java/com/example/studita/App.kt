package com.example.studita

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.studita.di.DI
import com.example.studita.di.data.*
import com.example.studita.di.data.exercise.ExercisesModule
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.CheckTokenIsCorrectStatus
import com.example.studita.domain.interactor.UserDataStatus
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.TimeUtils
import com.example.studita.utils.UserUtils
import com.example.studita.utils.launchExt
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
                GlobalScope.launch {
                    UserDataModule.getUserDataInteractorImpl().saveUserData(userDataData)
                }
            }
        }

        fun getUserData() {
            userDataDeferred = applicationScope.async {
                val userDataStatus = UserDataModule.getUserDataInteractorImpl()
                    .getUserData(PrefsUtils.getUserId(), PrefsUtils.isOfflineModeEnabled())

                withContext(Dispatchers.Main) {
                    if (userDataStatus is UserDataStatus.Success) {
                        initUserData(userDataStatus.result)
                    }
                    userDataStatus
                }
            }
        }

        fun authenticate(userIdTokenData: UserIdTokenData?, isOfflineModeChanged: Boolean) {
            authenticationState.value = CheckTokenIsCorrectStatus.Waiting to isOfflineModeChanged
            if (PrefsUtils.isOfflineModeEnabled() || !UserUtils.isLoggedIn()) {

                if(UserUtils.userDataIsNull())
                    getUserData()
                authenticationState.value = CheckTokenIsCorrectStatus.Correct to isOfflineModeChanged
            } else {
                authenticationJob = applicationScope.launchExt(authenticationJob) {
                    val tokenIsCorrectStatus = AuthorizationModule.getAuthorizationInteractorImpl()
                        .checkTokenIsCorrect(userIdTokenData!!)
                    getUserData()
                    authenticationState.postValue(tokenIsCorrectStatus to isOfflineModeChanged)
                    userDataDeferred.await()
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        DI.initialize(this)
        downloadCache()
        initDefPrefs()

        authenticate(UserUtils.getUserIDTokenData(), false)
    }

    private fun downloadCache() {
        GlobalScope.launch {
            LevelsModule.getLevelsInteractorImpl().downloadLevels()
            ChapterModule.getChapterInteractorImpl().downloadChapters()
            ExercisesModule.getExercisesInteractorImpl().downloadOfflineExercises()
            InterestingModule.getInterestingInteractorImpl().downloadInterestingList()
        }
    }

    private fun initDefPrefs() {
        if (!PrefsUtils.containsOfflineMode())
            PrefsUtils.setOfflineMode(false)
        if (!PrefsUtils.containsNotificationsMode())
            PrefsUtils.setNotificationsMode(true)
    }

}