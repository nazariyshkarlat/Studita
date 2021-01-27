package com.studita.presentation.view_model

import androidx.lifecycle.*
import com.studita.App
import com.studita.App.Companion.authenticationState
import com.studita.App.Companion.userDataDeferred
import com.studita.domain.entity.LevelChildData
import com.studita.domain.entity.LevelData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.interactor.CheckTokenIsCorrectStatus
import com.studita.domain.interactor.LevelsStatus
import com.studita.domain.interactor.SubscribeEmailResultStatus
import com.studita.domain.interactor.UserDataStatus
import com.studita.domain.interactor.levels.LevelsInteractor
import com.studita.domain.interactor.subscribe_email.SubscribeEmailInteractor
import com.studita.domain.interactor.user_data.UserDataInteractor
import com.studita.presentation.model.ErrorState
import com.studita.presentation.model.HomeRecyclerUiModel
import com.studita.presentation.model.toHomeRecyclerItems
import com.studita.service.SyncSubscribeEmailImpl
import com.studita.utils.PrefsUtils
import com.studita.utils.UserUtils
import com.studita.utils.launchExt
import kotlinx.coroutines.*
import org.koin.core.context.GlobalContext

class HomeFragmentViewModel : ViewModel() {

    val progressState = MutableLiveData<Boolean>()
    val errorEvent = SingleLiveEvent<ErrorState>()
    val logInSnackbarEvent = SingleLiveEvent<Boolean>()

    var results: List<LevelData>? = null
    var resultsAreLocal = false

    private val levelsInteractor = GlobalContext.get().get<LevelsInteractor>()
    private val subscribeEmailInteractor =GlobalContext.get().get<SubscribeEmailInteractor>()

    var levelsJob: Job? = null
    val subscribeEmailState = SingleLiveEvent<SubscribeEmailResultStatus>()

    init {
        App.offlineModeChangeEvent = LiveEvent()
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
                    results = getLevelsStatus.result

                    resultsAreLocal = PrefsUtils.isOfflineModeEnabled()

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
                results = getLevelsStatus.result
                progressState.value = false
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