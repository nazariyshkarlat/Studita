package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.di.data.UserStatisticsModule
import com.example.studita.domain.entity.UserStatisticsData
import com.example.studita.domain.interactor.UserStatisticsStatus
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.launchExt
import kotlinx.coroutines.Job

class UserStatisticsViewModel : ViewModel() {

    private val userStatisticsInteractor = UserStatisticsModule.getUserStatisticsInteractorImpl()

    val errorEvent = SingleLiveEvent<Boolean>()
    val userStatisticsState = MutableLiveData<List<UserStatisticsData>>()
    val progressState = MutableLiveData<Boolean>(true)

    private var job: Job? = null

    fun getUserStatistics(userId: Int) {
        job = viewModelScope.launchExt(job) {
            when (val status = userStatisticsInteractor.getUserStatistics(userId)) {
                is UserStatisticsStatus.NoConnection -> errorEvent.value = true
                is UserStatisticsStatus.ServiceUnavailable -> errorEvent.value = false
                is UserStatisticsStatus.Success -> {
                    userStatisticsState.value = status.results
                    progressState.value = false
                }
            }
        }
    }

}