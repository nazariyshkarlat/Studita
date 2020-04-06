package com.example.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.di.data.UserStatisticsModule
import com.example.studita.domain.enum.UserStatisticsTime
import com.example.studita.domain.interactor.LevelsStatus
import com.example.studita.domain.interactor.UserStatisticsStatus
import com.example.studita.presentation.model.mapper.LevelUiModelMapper
import com.example.studita.presentation.utils.PrefsUtils
import com.example.studita.presentation.utils.launchExt
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class UserStatisticsViewModel : ViewModel(){


    val errorState = SingleLiveEvent<Int>()
    val userStatisticsTodayState = MutableLiveData<UserStatisticsStatus>()
    val userStatisticsYesterdayState = MutableLiveData<UserStatisticsStatus>()
    val userStatisticsWeekState = MutableLiveData<UserStatisticsStatus>()
    val userStatisticsMonthState = MutableLiveData<UserStatisticsStatus>()

    private val interactor = UserStatisticsModule.getUserStatisticsInteractorImpl()

    fun getUserStatistics(userId: String, userToken: String, time: UserStatisticsTime){
        viewModelScope.launch{
            when(val status = interactor.getUserStatisticsInteractor(userId, userToken, time)){
                is UserStatisticsStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                is UserStatisticsStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                is UserStatisticsStatus.Failure -> errorState.postValue(R.string.server_failure)
                is UserStatisticsStatus.Success -> {
                    when(time) {
                        UserStatisticsTime.TODAY -> userStatisticsTodayState.postValue(status)
                        UserStatisticsTime.YESTERDAY -> userStatisticsYesterdayState.postValue(status)
                        UserStatisticsTime.WEEK -> userStatisticsWeekState.postValue(status)
                        UserStatisticsTime.MONTH -> userStatisticsMonthState.postValue(status)
                    }
                }
            }
        }
    }
}