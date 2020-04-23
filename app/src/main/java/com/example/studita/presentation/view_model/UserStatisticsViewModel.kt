package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.di.data.UserStatisticsModule
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.enum.UserStatisticsTime
import com.example.studita.domain.interactor.UserStatisticsStatus
import com.example.studita.presentation.utils.UserUtils
import com.example.studita.presentation.utils.launchExt
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class UserStatisticsViewModel : ViewModel(){

    val errorState = SingleLiveEvent<Int>()
    val userStatisticsState = MutableLiveData<UserStatisticsStatus>()

    private var job: Job? = null

    private val interactor = UserStatisticsModule.getUserStatisticsInteractorImpl()

    init {
        UserUtils.getUserTokenIdData()?.let { getUserStatistics(it) }
    }

    private fun getUserStatistics(userIdTokenData: UserIdTokenData){
        job = viewModelScope.launchExt(job){
            when(val status = interactor.getUserStatisticsInteractor(userIdTokenData)){
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