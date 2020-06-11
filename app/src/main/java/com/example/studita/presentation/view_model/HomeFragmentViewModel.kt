package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.service.SyncSubscribeEmailImpl
import com.example.studita.di.data.LevelsModule
import com.example.studita.di.data.SubscribeEmailModule
import com.example.studita.di.data.UserDataModule
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.LevelsStatus
import com.example.studita.domain.interactor.SubscribeEmailResultStatus
import com.example.studita.domain.interactor.UserDataStatus
import com.example.studita.presentation.model.HomeRecyclerUiModel
import com.example.studita.presentation.model.toHomeRecyclerItems
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.TimeUtils
import com.example.studita.utils.launchExt
import com.example.studita.utils.UserUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class HomeFragmentViewModel : ViewModel(){

    val progressState = MutableLiveData<Boolean>()
    val errorState = SingleLiveEvent<Int>()
    val subscribeEmailState =  SingleLiveEvent<SubscribeEmailResultStatus>()

    lateinit var results: List<HomeRecyclerUiModel>

    private val levelsInteractor = LevelsModule.getLevelsInteractorImpl()
    private val userDataInteractor = UserDataModule.getUserDataInteractorImpl()
    private val subscribeEmailInteractor = SubscribeEmailModule.getSubscribeEmailInteractorImpl()

    private var levelsJob: Job? = null
    private var subscribeJob: Job? = null

    private var userIdTokenData: UserIdTokenData? = null

    init{
        initSubscribeEmailState()
        userIdTokenData = UserUtils.getUserIDTokenData()
        getHomeScreenData()
    }

    private fun getHomeScreenData(){
        levelsJob = viewModelScope.launchExt(levelsJob){
            val statusDef = async{getUserData()}
            val userLevels = async { getUserLevels()}
            when(val status = statusDef.await()){
                is UserDataStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                is UserDataStatus.ServiceUnavailable ->errorState.postValue(R.string.server_unavailable)
                is UserDataStatus.Failure -> errorState.postValue(R.string.server_failure)
                else ->{
                    status as UserDataStatus.Success
                    initUserData(status.result)
                    UserUtils.userDataLiveData.postValue(status.result)

                    val userLevelsStatus = userLevels.await()
                    if(userLevelsStatus is LevelsStatus.Success) {
                        results = userLevelsStatus.result.map { it.toHomeRecyclerItems() }.flatten()
                        progressState.postValue(true)
                    }
                }
            }
        }
    }

    fun subscribeEmail(userIdTokenData: UserIdTokenData, subscribe: Boolean){
        subscribeJob = GlobalScope.launchExt(subscribeJob){
            when(val status = if(subscribe)
                subscribeEmailInteractor.subscribe(userIdTokenData)
            else
                subscribeEmailInteractor.unsubscribe(userIdTokenData)){
                is SubscribeEmailResultStatus.NoConnection ->{
                    subscribeEmailState.postValue(status)
                }
                is SubscribeEmailResultStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                is SubscribeEmailResultStatus.Success -> {
                    subscribeEmailState.postValue(status)
                }
            }
        }
    }


    private suspend fun getUserData() = userDataInteractor.getUserData(PrefsUtils.getUserId(), PrefsUtils.isOfflineMode())

    private suspend fun getUserLevels() = levelsInteractor.getLevels(UserUtils.isLoggedIn(), PrefsUtils.isOfflineMode())

    fun getRecyclerItems(userDataUiModel: HomeRecyclerUiModel.HomeUserDataUiModel, levels: List<HomeRecyclerUiModel>): List<HomeRecyclerUiModel>{
        val adapterItems = ArrayList<HomeRecyclerUiModel>()
        adapterItems.add(userDataUiModel)
        adapterItems.addAll(levels)
        return adapterItems
    }

    private fun initSubscribeEmailState(){
        SyncSubscribeEmailImpl.syncSubscribeEmailLiveData = subscribeEmailState
        subscribeEmailInteractor.getSyncedResult()?.let {
            subscribeEmailState.value = SubscribeEmailResultStatus.Success(
                it
            )
        }
    }

    private fun initUserData(userDataData: UserDataData){
        UserUtils.userDataLiveData.value = userDataData

        if(TimeUtils.getCalendarDayCount(userDataData.streakDatetime, Date()) > 1F){
            userDataData.streakDays = 0
            userDataData.todayCompletedExercises = 0
            viewModelScope.launch {
                userDataInteractor.saveUserData(userDataData)
            }
        }
    }
}