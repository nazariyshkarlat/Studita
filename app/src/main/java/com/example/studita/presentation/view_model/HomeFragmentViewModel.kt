package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.SyncSubscribeEmailImpl
import com.example.studita.di.data.LevelsModule
import com.example.studita.di.data.SubscribeEmailModule
import com.example.studita.di.data.UserDataModule
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.LevelsStatus
import com.example.studita.domain.interactor.SubscribeEmailResultStatus
import com.example.studita.domain.interactor.UserDataStatus
import com.example.studita.presentation.model.HomeRecyclerUiModel
import com.example.studita.presentation.model.mapper.LevelUiModelMapper
import com.example.studita.presentation.utils.launchExt
import com.example.studita.presentation.utils.UserUtils
import kotlinx.coroutines.Job

class HomeFragmentViewModel : ViewModel(){

    val progressState = MutableLiveData<Boolean>()
    val errorState = SingleLiveEvent<Int>()
    val userDataState = SingleLiveEvent<UserDataData>()
    val subscribeEmailState =  SingleLiveEvent<SubscribeEmailResultStatus>()

    lateinit var results: List<HomeRecyclerUiModel>

    private val levelsInteractor = LevelsModule.getLevelsInteractorImpl()
    private val userDataInteractor = UserDataModule.getUserDataInteractorImpl()
    private val subscribeEmailInteractor = SubscribeEmailModule.getSubscribeEmailInteractorImpl()

    private var levelsJob: Job? = null
    private var subscribeJob: Job? = null

    var userIdTokenData: UserIdTokenData? = null

    init{
        initSubscribeEmailState()
        userIdTokenData = UserUtils.getUserTokenIdData()
        getLevels(userIdTokenData)
    }

    private fun getLevels(userIdTokenData: UserIdTokenData?){
        levelsJob = viewModelScope.launchExt(levelsJob){
            getUserData(userIdTokenData)
            when(val status = levelsInteractor.getLevels(UserUtils.isLoggedIn())){
                is LevelsStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                is LevelsStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                is LevelsStatus.Success -> {
                    progressState.postValue(true)
                    results = LevelUiModelMapper()
                        .map(status.result)
                }
            }
        }
    }

    fun subscribeEmail(userIdTokenData: UserIdTokenData, subscribe: Boolean){
        subscribeJob = viewModelScope.launchExt(subscribeJob){
            when(val status = if(subscribe) subscribeEmailInteractor.subscribe(userIdTokenData) else  subscribeEmailInteractor.unsubscribe(userIdTokenData)){
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


    private suspend fun getUserData(userIdTokenData: UserIdTokenData?){
        val userData = userDataInteractor.getUserData(userIdTokenData)
        if (userData is UserDataStatus.Success) {
            userDataState.postValue(userData.result)
        }
    }

    fun getRecyclerItems(userDataUiModel: HomeRecyclerUiModel.HomeUserDataUiModel, levels: List<HomeRecyclerUiModel>): List<HomeRecyclerUiModel>{
        val adapterItems = ArrayList<HomeRecyclerUiModel>()
        adapterItems.add(userDataUiModel)
        adapterItems.addAll(levels)
        return adapterItems
    }

    private fun initSubscribeEmailState(){
        SyncSubscribeEmailImpl.syncSubscribeEmailLiveData = subscribeEmailState
        subscribeEmailState.value = subscribeEmailInteractor.getSyncedResult()?.let {
            SubscribeEmailResultStatus.Success(
                it
            )
        }
    }
}