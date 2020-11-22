package com.studita.presentation.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studita.App
import com.studita.di.data.UserStatisticsModule
import com.studita.domain.entity.UserStatisticsData
import com.studita.domain.interactor.UserDataStatus
import com.studita.domain.interactor.UserStatisticsStatus
import com.studita.utils.PrefsUtils
import com.studita.utils.UserUtils
import com.studita.utils.launchExt
import kotlinx.coroutines.Job

class UserStatisticsViewModel(val userId: Int) : ViewModel() {

    private val userStatisticsInteractor = UserStatisticsModule.getUserStatisticsInteractorImpl()

    val errorEvent = SingleLiveEvent<Boolean>()
    val userStatisticsState = MutableLiveData<List<UserStatisticsData>>()
    val progressState = MutableLiveData<Boolean>(true)

    private var job: Job? = null

    init {
        getUserStatistics()
    }

    fun getUserStatistics() {

        job = viewModelScope.launchExt(job) {


            if(App.userDataDeferred.isCompleted && App.userDataDeferred.await() !is UserDataStatus.Success)
                App.authenticate(UserUtils.getUserIDTokenData(), true)

            when (val status = userStatisticsInteractor.getUserStatistics(userId)) {
                is UserStatisticsStatus.NoConnection -> errorEvent.value = true
                is UserStatisticsStatus.ServiceUnavailable -> errorEvent.value = false
                is UserStatisticsStatus.Success -> {

                    when(App.userDataDeferred.await()) {
                        is UserDataStatus.Success -> {
                            userStatisticsState.value = status.results
                            progressState.value = false
                        }
                        is UserDataStatus.NoConnection -> {
                            errorEvent.value = true
                        }
                        else -> {
                            errorEvent.value = false
                        }
                    }
                }
            }
        }
    }

}