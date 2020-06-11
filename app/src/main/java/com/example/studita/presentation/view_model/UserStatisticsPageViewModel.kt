package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.di.data.UserStatisticsModule
import com.example.studita.domain.interactor.UserStatisticsStatus
import com.example.studita.utils.launchExt
import kotlinx.coroutines.Job

class UserStatisticsPageViewModel : ViewModel(){

    private val userStatisticsInteractor = UserStatisticsModule.getUserStatisticsInteractorImpl()

    val errorState = SingleLiveEvent<Int>()
    val userStatisticsState = MutableLiveData<UserStatisticsStatus>()

    private var job: Job? = null

    fun getUserStatistics(userId: Int){
        job = viewModelScope.launchExt(job){
            when(val status = userStatisticsInteractor.getUserStatistics(userId)){
                is UserStatisticsStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                is UserStatisticsStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                is UserStatisticsStatus.Failure -> errorState.postValue(R.string.server_failure)
                is UserStatisticsStatus.Success -> {
                    userStatisticsState.postValue(status)
                }
            }
        }
    }

}