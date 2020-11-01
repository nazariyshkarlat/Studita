package com.example.studita.presentation.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.App
import com.example.studita.R
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
import com.example.studita.service.SyncSubscribeEmailImpl
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.UserUtils
import com.example.studita.utils.launchExt
import kotlinx.coroutines.*

class HomeFragmentViewModel : ViewModel() {

    val progressState = MutableLiveData<Boolean>()
    val errorEvent = SingleLiveEvent<Boolean>()
    val subscribeEmailState = SingleLiveEvent<SubscribeEmailResultStatus>()
    val subscribeErrorEvent = SingleLiveEvent<Boolean>()
    val logInSnackbarEvent = SingleLiveEvent<Boolean>()

    var results: List<HomeRecyclerUiModel>? = null

    private val userDataInteractor = UserDataModule.getUserDataInteractorImpl()
    private val levelsInteractor = LevelsModule.getLevelsInteractorImpl()
    private val subscribeEmailInteractor = SubscribeEmailModule.getSubscribeEmailInteractorImpl()
    val localUserDataState = MutableLiveData<UserDataData>()

    var levelsJob: Job? = null
    private var subscribeJob: Job? = null

    init {
        if(!PrefsUtils.isOfflineModeEnabled() && UserUtils.isLoggedIn())
            getUserDataLocal()
        getLevels()
    }

    fun getLevels() {
        progressState.value = false
        levelsJob = viewModelScope.launchExt(levelsJob) {
            when (val getLevelsStatus = levelsInteractor.getLevels(
                UserUtils.isLoggedIn(),
                PrefsUtils.isOfflineModeEnabled()
            )) {
                is LevelsStatus.NoConnection -> {
                    errorEvent.value = true
                }
                is LevelsStatus.ServiceUnavailable -> {
                    errorEvent.value = false
                }
                is LevelsStatus.Failure -> {

                }
                else -> {
                    getLevelsStatus as LevelsStatus.Success
                    results = getLevelsStatus.result.map { it.toHomeRecyclerItems() }.flatten()

                    if(UserUtils.userDataNotNull()) {
                        progressState.value = true
                    }
                }
            }
        }
    }

    fun getOfflineLevels() {
        progressState.value = false
        levelsJob = viewModelScope.launchExt(levelsJob) {
            when (val getLevelsStatus = levelsInteractor.getLevels(
                UserUtils.isLoggedIn(),
                true
            )) {
                is LevelsStatus.NoConnection -> {
                    errorEvent.value = true
                }
                is LevelsStatus.ServiceUnavailable -> {
                    errorEvent.value = false
                }
                is LevelsStatus.Failure -> {

                }
                else -> {
                    getLevelsStatus as LevelsStatus.Success
                    results = getLevelsStatus.result.map { it.toHomeRecyclerItems() }.flatten()
                    progressState.value = true
                }
            }
        }
    }


    fun subscribeEmail(userIdTokenData: UserIdTokenData, subscribe: Boolean) {
        subscribeJob = GlobalScope.launchExt(subscribeJob) {
            when (val status = if (subscribe)
                subscribeEmailInteractor.subscribe(userIdTokenData)
            else
                subscribeEmailInteractor.unsubscribe(userIdTokenData)) {
                is SubscribeEmailResultStatus.NoConnection -> {
                    subscribeEmailState.value = status
                }
                is SubscribeEmailResultStatus.Success -> {
                    subscribeEmailState.value = status
                }
                else -> {
                    subscribeErrorEvent.value = true
                }
            }
        }
    }

    fun clearResults(){
        results = null
    }

    fun showLogInSnackbar(isAfterSignUp: Boolean){
        viewModelScope.launch(Dispatchers.Main) {
            logInSnackbarEvent.value = isAfterSignUp
        }
    }


    fun getRecyclerItems(
        userDataUiModel: HomeRecyclerUiModel.HomeUserDataUiModel,
        levels: List<HomeRecyclerUiModel>
    ): List<HomeRecyclerUiModel> {
        val adapterItems = ArrayList<HomeRecyclerUiModel>()
        adapterItems.add(userDataUiModel)
        adapterItems.addAll(levels)
        return adapterItems
    }

    fun initSubscribeEmailState() {
        SyncSubscribeEmailImpl.syncSubscribeEmailLiveData = subscribeEmailState
        subscribeEmailInteractor.getSyncedResult()?.let {
            viewModelScope.launch(Dispatchers.Main) {
                delay(1000L)
                subscribeEmailState.value = SubscribeEmailResultStatus.Success(
                    it
                )
            }
        }
    }

    private fun getUserDataLocal(){
        viewModelScope.launch(Dispatchers.Main) {
            val userDataStatus = userDataInteractor.getUserData(
                PrefsUtils.getUserId()!!, true, true)
            if(userDataStatus is UserDataStatus.Success)
                localUserDataState.value = userDataStatus.result
        }
    }
}