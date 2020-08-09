package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.App
import com.example.studita.R
import com.example.studita.di.data.LevelsModule
import com.example.studita.di.data.SubscribeEmailModule
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.LevelsStatus
import com.example.studita.domain.interactor.SubscribeEmailResultStatus
import com.example.studita.presentation.model.HomeRecyclerUiModel
import com.example.studita.presentation.model.toHomeRecyclerItems
import com.example.studita.service.SyncSubscribeEmailImpl
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.UserUtils
import com.example.studita.utils.launchExt
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job

class HomeFragmentViewModel : ViewModel() {

    val progressState = MutableLiveData<Boolean>()
    val errorState = SingleLiveEvent<Int>()
    val subscribeEmailState = SingleLiveEvent<SubscribeEmailResultStatus>()

    lateinit var results: List<HomeRecyclerUiModel>

    private val levelsInteractor = LevelsModule.getLevelsInteractorImpl()
    private val subscribeEmailInteractor = SubscribeEmailModule.getSubscribeEmailInteractorImpl()

    private var levelsJob: Job? = null
    private var subscribeJob: Job? = null

    fun getLevels() {
        progressState.postValue(false)
        levelsJob = viewModelScope.launchExt(levelsJob) {
            when (val getLevelsStatus = levelsInteractor.getLevels(
                UserUtils.isLoggedIn(),
                PrefsUtils.isOfflineModeEnabled()
            )) {
                is LevelsStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                is LevelsStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                is LevelsStatus.Failure -> errorState.postValue(R.string.server_failure)
                else -> {
                    getLevelsStatus as LevelsStatus.Success
                    results = getLevelsStatus.result.map { it.toHomeRecyclerItems() }.flatten()

                    App.userDataDeferred.await()
                    progressState.postValue(true)
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
                    subscribeEmailState.postValue(status)
                }
                is SubscribeEmailResultStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                is SubscribeEmailResultStatus.Success -> {
                    subscribeEmailState.postValue(status)
                }
            }
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
            subscribeEmailState.value = SubscribeEmailResultStatus.Success(
                it
            )
        }
    }
}