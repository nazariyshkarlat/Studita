package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.di.LevelsModule
import com.example.studita.di.UserDataModule
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.interactor.LevelsStatus
import com.example.studita.domain.interactor.UserDataStatus
import com.example.studita.presentation.utils.launchExt
import com.example.studita.presentation.model.LevelUiModel
import com.example.studita.presentation.model.mapper.LevelUiModelMapper
import com.example.studita.presentation.utils.PrefsUtils
import kotlinx.coroutines.Job

class HomeFragmentViewModel : ViewModel(){

    val progressState = MutableLiveData<Boolean>()
    val errorState = SingleLiveEvent<Int>()
    val userDataState = MutableLiveData<UserDataData>()

    lateinit var results: List<LevelUiModel>

    private val levelsInteractor = LevelsModule.getLevelsInteractorImpl()
    private val userDataInteractor = UserDataModule.getUserDataInteractorImpl()

    private var levelsJob: Job? = null
    private var userDataJob: Job? = null

    var userId: String? = null
    var userToken: String? = null



    init{
        getLevels()

        userId = PrefsUtils.getUserId()
        userToken = PrefsUtils.getUserToken()
        getUserData(userId, userToken)
    }

    private fun getLevels(){
        levelsJob = viewModelScope.launchExt(levelsJob){
            progressState.postValue(false)
            when(val status = levelsInteractor.getLevels()){
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


    private fun getUserData(userId: String?, userToken: String?){
        userDataJob = viewModelScope.launchExt(userDataJob){
            userId?.let {
                userToken?.let {
                    val userData = userDataInteractor.getUserData(userId, userToken)
                    if (userData is UserDataStatus.Success) {
                        userDataState.postValue(userData.result)
                    }
                }
            }
        }
    }
}