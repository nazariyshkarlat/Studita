package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.di.data.LevelsModule
import com.example.studita.di.data.SubscribeEmailModule
import com.example.studita.di.data.UserDataModule
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserTokenIdData
import com.example.studita.domain.interactor.LevelsStatus
import com.example.studita.domain.interactor.SubscribeEmailStatus
import com.example.studita.domain.interactor.UserDataStatus
import com.example.studita.presentation.model.HomeRecyclerUiModel
import com.example.studita.presentation.model.mapper.LevelUiModelMapper
import com.example.studita.presentation.utils.launchExt
import com.example.studita.presentation.utils.UserUtils
import kotlinx.coroutines.Job

class HomeFragmentViewModel : ViewModel(){

    val progressState = MutableLiveData<Boolean>()
    val errorState = SingleLiveEvent<Int>()
    val userDataState = MutableLiveData<UserDataData>()
    val subscribeEmailState =  SingleLiveEvent<Pair<Boolean, SubscribeEmailStatus>>()

    lateinit var results: List<HomeRecyclerUiModel>

    private val levelsInteractor = LevelsModule.getLevelsInteractorImpl()
    private val userDataInteractor = UserDataModule.getUserDataInteractorImpl()
    private val subscribeEmailInteractor = SubscribeEmailModule.getSubscribeEmailInteractorImpl()

    private var levelsJob: Job? = null
    private var subscribeJob: Job? = null

    var userTokenIdData: UserTokenIdData? = null



    init{
        userTokenIdData = UserUtils.getUserTokenIdData()
        getLevels(userTokenIdData)
    }

    private fun getLevels(userTokenIdData: UserTokenIdData?){
        levelsJob = viewModelScope.launchExt(levelsJob){
            progressState.postValue(false)
            getUserData(userTokenIdData)
            when(val status = levelsInteractor.getLevels(UserUtils.isLoggedIn())){
                is LevelsStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                is LevelsStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                is LevelsStatus.Success -> {
                    progressState.postValue(true)
                    results = LevelUiModelMapper()
                        .map(status.result)
                    println(results)
                }
            }
        }
    }

    fun subscribeEmail(userTokenIdData: UserTokenIdData, subscribe: Boolean){
        subscribeJob = viewModelScope.launchExt(subscribeJob){
            when(val status = if(subscribe) subscribeEmailInteractor.subscribe(userTokenIdData) else  subscribeEmailInteractor.unsubscribe(userTokenIdData)){
                is SubscribeEmailStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                is SubscribeEmailStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                is SubscribeEmailStatus.Success -> {
                    subscribeEmailState.postValue(subscribe to status)
                }
            }
        }
    }


    private suspend fun getUserData(userTokenIdData: UserTokenIdData?){
        userTokenIdData?.let {
            val userData = userDataInteractor.getUserData(userTokenIdData)
            if (userData is UserDataStatus.Success) {
                userDataState.postValue(userData.result)
            }
        }
    }

    fun getRecyclerItems(userDataUiModel: HomeRecyclerUiModel.HomeUserDataUiModel, levels: List<HomeRecyclerUiModel>): List<HomeRecyclerUiModel>{
        val adapterItems = ArrayList<HomeRecyclerUiModel>()
        adapterItems.add(userDataUiModel)
        adapterItems.addAll(levels)
        return adapterItems
    }
}