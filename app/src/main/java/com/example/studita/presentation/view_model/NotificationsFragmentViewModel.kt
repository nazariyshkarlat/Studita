package com.example.studita.presentation.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.di.data.NotificationsModule
import com.example.studita.domain.entity.NotificationData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.GetNotificationsStatus
import com.example.studita.presentation.model.NotificationsUiModel
import com.example.studita.presentation.model.toUiModel
import com.example.studita.utils.UserUtils
import com.example.studita.utils.launchExt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NotificationsFragmentViewModel : ViewModel() {

    private val notificationsInteractor = NotificationsModule.getNotificationsInteractorImpl()

    val notificationsState = MutableLiveData<Pair<Boolean, NotificationsResultState>>()
    val progressState = MutableLiveData<Boolean>()

    val errorEvent = SingleLiveEvent<Boolean>()
    var errorState = false

    var recyclerItems: ArrayList<NotificationsUiModel>? = null

    private var notificationsAreCheckedJob: Job? = null
    private var getNotificationsJob: Job? = null

    val perPage = 20
    var currentPageNumber = 1


    init {
        getNotifications(UserUtils.getUserIDTokenData()!!, false)
    }

    fun getNotifications(userIdTokenData: UserIdTokenData, newPage: Boolean) {

        errorState = false

        if (newPage)
            currentPageNumber++

        getNotificationsJob = viewModelScope.launchExt(getNotificationsJob) {
            when (val result = notificationsInteractor.getNotifications(
                userIdTokenData,
                perPage,
                currentPageNumber
            )) {
                is GetNotificationsStatus.NoConnection -> {
                    errorState = true
                    errorEvent.value = true
                }
                is GetNotificationsStatus.ServiceUnavailable -> {
                    errorState = true
                    errorEvent.value = false
                }
                is GetNotificationsStatus.NoNotificationsFound -> {
                    progressState.value = false
                    notificationsState.value = false to (if (currentPageNumber == 1) NotificationsResultState.NoResultsFound else NotificationsResultState.NoMoreResultsFound)
                }
                is GetNotificationsStatus.Success -> {
                    val notificationsResultState =
                        if (currentPageNumber == 1) NotificationsResultState.FirstResults(result.notificationsData) else NotificationsResultState.MoreResults(
                            result.notificationsData
                        )

                    if (UserUtils.userDataNotNull() && !UserUtils.userData.notificationsAreChecked) {
                        UserUtils.userDataLiveData.value = UserUtils.userData.apply {
                            notificationsAreChecked = true
                        }
                    }
                    progressState.value = false
                    notificationsState.value = canBeMoreItems(notificationsResultState) to notificationsResultState
                }
            }
        }
    }

    fun setNotificationsAreChecked(userIdTokenData: UserIdTokenData) {
        notificationsAreCheckedJob = viewModelScope.launchExt(notificationsAreCheckedJob) {
            notificationsInteractor.setNotificationsAreChecked(userIdTokenData)
        }
    }

    private fun canBeMoreItems(notificationsResultState: NotificationsResultState) =
        (((notificationsResultState is NotificationsResultState.FirstResults) && notificationsResultState.results.size == perPage) ||
                ((notificationsResultState is NotificationsResultState.MoreResults) && notificationsResultState.results.size == perPage))

    fun getRecyclerItems(
        notificationSwitch: NotificationsUiModel.NotificationsSwitch,
        notificationItems: List<NotificationData>,
        progressItem: NotificationsUiModel.ProgressUiModel? = null,
        context: Context
    ): ArrayList<NotificationsUiModel> {
        val adapterItems = ArrayList<NotificationsUiModel>()
        adapterItems.add(notificationSwitch)
        adapterItems.addAll(notificationItems.map { it.toUiModel(context) })
        if (progressItem != null)
            adapterItems.add(progressItem)
        return adapterItems
    }

    sealed class NotificationsResultState {
        data class FirstResults(val results: List<NotificationData>) : NotificationsResultState()
        data class MoreResults(val results: List<NotificationData>) : NotificationsResultState()
        object NoResultsFound : NotificationsResultState()
        object NoMoreResultsFound : NotificationsResultState()
    }

}