package com.studita.presentation.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studita.App
import com.studita.domain.entity.NotificationData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.interactor.GetNotificationsStatus
import com.studita.domain.interactor.UserDataStatus
import com.studita.domain.interactor.notifications.NotificationsInteractor
import com.studita.domain.interactor.user_data.UserDataInteractor
import com.studita.presentation.model.NotificationsUiModel
import com.studita.presentation.model.toUiModel
import com.studita.utils.UserUtils
import com.studita.utils.launchExt
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import org.koin.core.context.GlobalContext

class NotificationsFragmentViewModel : ViewModel() {

    private val notificationsInteractor = GlobalContext.get().get<NotificationsInteractor>()

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


            if(App.userDataDeferred.isCompleted && App.userDataDeferred.await() !is UserDataStatus.Success)
                App.authenticate(UserUtils.getUserIDTokenData(), true)

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
                    when(App.userDataDeferred.await()) {
                        is UserDataStatus.Success -> {
                            progressState.value = false
                            notificationsState.value = false to (if (currentPageNumber == 1) NotificationsResultState.NoResultsFound else NotificationsResultState.NoMoreResultsFound)
                        }
                        is UserDataStatus.NoConnection -> {
                            errorState = true
                            errorEvent.value = true
                        }
                        else -> {
                            errorState = true
                            errorEvent.value = false
                        }
                    }
                }
                is GetNotificationsStatus.Success -> {

                    when(App.userDataDeferred.await()) {
                        is UserDataStatus.Success -> {

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
                            notificationsState.value =
                                canBeMoreItems(notificationsResultState) to notificationsResultState
                        }
                        is UserDataStatus.NoConnection -> {
                            errorState = true
                            errorEvent.value = true
                        }
                        else -> {
                            errorState = true
                            errorEvent.value = false
                        }
                    }
                }
            }
        }
    }

    fun notificationsRequestIsPending() = getNotificationsJob?.isActive == true

    fun setNotificationsAreChecked(userIdTokenData: UserIdTokenData) {
        notificationsAreCheckedJob = GlobalScope.launchExt(notificationsAreCheckedJob) {
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