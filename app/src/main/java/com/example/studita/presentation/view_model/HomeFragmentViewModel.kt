package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.di.data.LevelsModule
import com.example.studita.di.data.UserDataModule
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.interactor.LevelsStatus
import com.example.studita.domain.interactor.UserDataStatus
import com.example.studita.presentation.model.HomeRecyclerUiModel
import com.example.studita.presentation.utils.launchExt
import com.example.studita.presentation.model.mapper.LevelUiModelMapper
import com.example.studita.presentation.utils.PrefsUtils
import kotlinx.coroutines.Job

class HomeFragmentViewModel : ViewModel(){

    val progressState = MutableLiveData<Boolean>()
    val errorState = SingleLiveEvent<Int>()
    val userDataState = MutableLiveData<UserDataData>()

    lateinit var results: List<HomeRecyclerUiModel>

    private val levelsInteractor = LevelsModule.getLevelsInteractorImpl()
    private val userDataInteractor = UserDataModule.getUserDataInteractorImpl()

    private var levelsJob: Job? = null

    var userId: String? = null
    var userToken: String? = null



    init{
        userId = PrefsUtils.getUserId()
        userToken = PrefsUtils.getUserToken()
        getLevels(userId, userToken)
    }

    private fun getLevels(userId: String?, userToken: String?){
        levelsJob = viewModelScope.launchExt(levelsJob){
            progressState.postValue(false)
            getUserData(userId, userToken)
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


    private suspend fun getUserData(userId: String?, userToken: String?){
        userId?.let {
            userToken?.let {
                val userData = userDataInteractor.getUserData(userId, userToken)
                if (userData is UserDataStatus.Success) {
                    userDataState.postValue(userData.result)
                }
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