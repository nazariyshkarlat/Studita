package com.example.studita

import android.app.Application
import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.studita.data.entity.FriendActionRequest
import com.example.studita.di.DI
import com.example.studita.di.data.*
import com.example.studita.di.data.exercise.ExercisesModule
import com.example.studita.domain.entity.FriendActionRequestData
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.entity.authorization.AuthorizationRequestData
import com.example.studita.domain.interactor.PrivacySettingsDuelsExceptionsStatus
import com.example.studita.domain.interactor.PrivacySettingsStatus
import com.example.studita.domain.interactor.UserDataStatus
import com.example.studita.domain.interactor.user_data.UserDataInteractor
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.TimeUtils
import com.example.studita.utils.UserUtils
import kotlinx.coroutines.*
import java.util.*

class App : Application(){

    companion object{
        lateinit var userDataDeferred: Deferred<UserDataStatus>
        val applicationScope = CoroutineScope(Dispatchers.Main)

        fun initUserData(userDataData: UserDataData){
            UserUtils.userDataLiveData.value = userDataData

            if(TimeUtils.getCalendarDayCount(userDataData.streakDatetime, Date()) > 1F){
                userDataData.streakDays = 0
                userDataData.todayCompletedExercises = 0
                GlobalScope.launch {
                    UserDataModule.getUserDataInteractorImpl().saveUserData(userDataData)
                }
            }
        }

        fun getUserData(){
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
    }

    override fun onCreate() {
        super.onCreate()
        DI.initialize(this)
        downloadCache()
        initDefPrefs()

        getUserData()
        applicationScope.launch {
            userDataDeferred.await()
        }
    }

    private fun downloadCache(){
        GlobalScope.launch {
            LevelsModule.getLevelsInteractorImpl().downloadLevels()
            ChapterModule.getChapterInteractorImpl().downloadChapters()
            ExercisesModule.getExercisesInteractorImpl().downloadOfflineExercises()
            InterestingModule.getInterestingInteractorImpl().downloadInterestingList()
        }
    }

    private fun initDefPrefs(){
        if(!PrefsUtils.containsOfflineMode())
            PrefsUtils.setOfflineMode(false)
        if(!PrefsUtils.containsNotificationsMode())
            PrefsUtils.setNotificationsMode(true)
    }

}