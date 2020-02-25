package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.di.LevelsModule
import com.example.studita.domain.interactor.LevelsStatus
import com.example.studita.presentation.extensions.launchExt
import com.example.studita.presentation.model.LevelUiModel
import com.example.studita.presentation.model.mapper.LevelUiModelMapper
import kotlinx.coroutines.Job

class HomeFragmentViewModel : ViewModel(){

    val progressState = MutableLiveData<Boolean>()
    val errorState = SingleLiveEvent<Int>()

    lateinit var results: List<LevelUiModel>
    private val interactor = LevelsModule.getLevelsInteractorImpl()

    private var job: Job? = null

    init{
        getLevels()
    }

    private fun getLevels(){
        job = viewModelScope.launchExt(job){
            progressState.postValue(false)
            when(val status = interactor.getLevels()){
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
}