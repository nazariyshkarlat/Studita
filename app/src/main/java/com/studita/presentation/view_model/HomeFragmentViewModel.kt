package com.studita.presentation.view_model

import android.util.Log
import androidx.lifecycle.*
import com.studita.App
import com.studita.App.Companion.authenticationState
import com.studita.App.Companion.userDataDeferred
import com.studita.R
import com.studita.di.data.LevelsModule
import com.studita.di.data.SubscribeEmailModule
import com.studita.di.data.UserDataModule
import com.studita.domain.entity.UserDataData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.interactor.CheckTokenIsCorrectStatus
import com.studita.domain.interactor.LevelsStatus
import com.studita.domain.interactor.SubscribeEmailResultStatus
import com.studita.domain.interactor.UserDataStatus
import com.studita.presentation.model.ErrorState
import com.studita.presentation.model.HomeRecyclerUiModel
import com.studita.presentation.model.toHomeRecyclerItems
import com.studita.service.SyncSubscribeEmailImpl
import com.studita.utils.PrefsUtils
import com.studita.utils.UserUtils
import com.studita.utils.launchExt
import kotlinx.coroutines.*

class HomeFragmentViewModel : ViewModel() {

    val progressState = MutableLiveData<Boolean>()
    val errorEvent = SingleLiveEvent<ErrorState>()
    val subscribeEmailState = SingleLiveEvent<SubscribeEmailResultStatus>()
    val subscribeErrorEvent = SingleLiveEvent<Boolean>()
    val logInSnackbarEvent = SingleLiveEvent<Boolean>()

    var results: List<HomeRecyclerUiModel>? = null
    var resultsAreLocal = false

    private val levelsInteractor = LevelsModule.getLevelsInteractorImpl()
    private val subscribeEmailInteractor = SubscribeEmailModule.getSubscribeEmailInteractorImpl()

    var levelsJob: Job? = null
    private var subscribeJob: Job? = null

    init {
        App.offlineModeChangeEvent = SingleLiveEvent()
        getLevels()
    }

    fun getLevels() {
        progressState.value = true
        levelsJob = viewModelScope.launchExt(levelsJob) {
            val getLevelsStatus = levelsInteractor.getLevels(
                UserUtils.isLoggedIn(),
                PrefsUtils.isOfflineModeEnabled()
            )
            when (getLevelsStatus) {
                is LevelsStatus.NoConnection -> {
                    println(errorEvent.value)
                    if(errorEvent.value != ErrorState.CONNECTION_ERROR)
                        errorEvent.value = ErrorState.CONNECTION_ERROR
                }
                is LevelsStatus.ServiceUnavailable -> {
                    if(errorEvent.value != ErrorState.SERVER_ERROR)
                        errorEvent.value = ErrorState.SERVER_ERROR
                }
                is LevelsStatus.Failure -> {

                }
                else -> {
                    getLevelsStatus as LevelsStatus.Success
                    results = getLevelsStatus.result.map { it.toHomeRecyclerItems() }.flatten()

                    resultsAreLocal = PrefsUtils.isOfflineModeEnabled()

                    println(userDataDeferred.await())
                    if(userDataDeferred.await() is UserDataStatus.Success && authenticationState.value?.first is CheckTokenIsCorrectStatus.Correct) {
                        progressState.value = false
                        errorEvent.value = ErrorState.NO_ERROR
                    }else{
                        errorEvent.value = if(userDataDeferred.await() is UserDataStatus.NoConnection) ErrorState.CONNECTION_ERROR else ErrorState.SERVER_ERROR
                    }
                }
            }
        }
    }

    fun getOfflineLevels() {
        levelsJob = viewModelScope.launchExt(levelsJob) {
            val getLevelsStatus = levelsInteractor.getLevels(
                UserUtils.isLoggedIn(),
                true
            )
            if (getLevelsStatus is LevelsStatus.Success) {
                resultsAreLocal = true
                results = getLevelsStatus.result.map { it.toHomeRecyclerItems() }.flatten()
                progressState.value = false
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
}