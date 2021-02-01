package com.studita.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.interactor.SubscribeEmailResultStatus
import com.studita.domain.interactor.subscribe_email.SubscribeEmailInteractor
import com.studita.domain.interactor.user_data.UserDataInteractor
import com.studita.utils.UserUtils
import com.studita.utils.launchExt
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job

class SelectCourseFragmentViewModel(
    private val subscribeEmailInteractor: SubscribeEmailInteractor,
    private val userDataInteractor: UserDataInteractor
) : ViewModel() {

    private var subscribeJob: Job? = null
    val subscribeEmailEvent = SingleLiveEvent<SubscribeEmailResultStatus>()
    val subscribeEmailErrorEvent = SingleLiveEvent<Boolean>()

    fun subscribeEmail(userIdTokenData: UserIdTokenData, subscribe: Boolean) {
        UserUtils.userDataLiveData.value = UserUtils.userDataLiveData.value.apply {
            this!!.isSubscribed = subscribe
        }
        subscribeJob = viewModelScope.launchExt(subscribeJob) {
            when (val status = if (subscribe)
                subscribeEmailInteractor.subscribe(userIdTokenData)
            else
                subscribeEmailInteractor.unsubscribe(userIdTokenData)) {
                is SubscribeEmailResultStatus.NoConnection -> {
                    subscribeEmailEvent.value = status
                }
                is SubscribeEmailResultStatus.Success -> {
                    userDataInteractor.saveUserData(UserUtils.userData)
                    subscribeEmailEvent.value = status
                }
                else -> {
                    subscribeEmailErrorEvent.value = true
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

}