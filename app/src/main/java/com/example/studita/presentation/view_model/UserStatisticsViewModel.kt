package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.di.data.UserDataModule
import com.example.studita.di.data.UserStatisticsModule
import com.example.studita.domain.interactor.UserDataStatus
import com.example.studita.domain.interactor.UserStatisticsStatus
import com.example.studita.utils.launchExt
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class UserStatisticsViewModel : ViewModel(){

    private val userDataInteractor = UserDataModule.getUserDataInteractorImpl()

    val userDataState = MutableLiveData<UserDataStatus>()

    fun getUserData(userId: Int) {
        viewModelScope.launch {
             userDataState.postValue(userDataInteractor.getUserData(userId, false))
        }
    }
}