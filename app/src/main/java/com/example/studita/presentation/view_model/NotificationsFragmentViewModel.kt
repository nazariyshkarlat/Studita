package com.example.studita.presentation.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.di.data.NotificationsModule
import com.example.studita.di.data.PrivacySettingsModule
import com.example.studita.domain.entity.NotificationData
import com.example.studita.domain.entity.PrivacyDuelsExceptionData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.GetNotificationsStatus
import com.example.studita.domain.interactor.PrivacySettingsDuelsExceptionsStatus
import com.example.studita.presentation.model.NotificationsUiModel
import com.example.studita.presentation.model.PrivacySettingsDuelsExceptionsRecyclerUiModel
import com.example.studita.presentation.model.toUiModel
import com.example.studita.utils.UserUtils
import kotlinx.coroutines.launch

class NotificationsFragmentViewModel : ViewModel(){

    private val notificationsInteractor = NotificationsModule.getNotificationsInteractorImpl()

    val notificationsState = MutableLiveData<Pair<Boolean, NotificationsResultState>>()
    val errorState = SingleLiveEvent<Int>()
    val progressState = MutableLiveData<Boolean>()

    var recyclerItems: ArrayList<NotificationsUiModel>? = null

    private val perPage = 20
    var currentPageNumber = 1


    init {
        getNotifications(UserUtils.getUserIDTokenData()!!, false)
    }

    fun getNotifications(userIdTokenData: UserIdTokenData, newPage: Boolean){

        if(newPage)
            currentPageNumber++
        else
            currentPageNumber=1

        viewModelScope.launch{
            when(val result = notificationsInteractor.getNotifications(userIdTokenData, perPage, currentPageNumber)){
                is GetNotificationsStatus.Failure -> errorState.postValue(R.string.no_connection)
                is GetNotificationsStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                is GetNotificationsStatus.ServiceUnavailable -> errorState.postValue(R.string.no_connection)
                is GetNotificationsStatus.NoNotificationsFound -> {
                    progressState.postValue(false)
                    notificationsState.postValue(false to (if(currentPageNumber == 1) NotificationsResultState.NoResultsFound else NotificationsResultState.NoMoreResultsFound))
                }
                is GetNotificationsStatus.Success -> {

                    val notificationsResultState = if(currentPageNumber == 1) NotificationsResultState.FirstResults(result.notificationsData) else NotificationsResultState.MoreResults(result.notificationsData)

                    UserUtils.userDataLiveData.postValue(UserUtils.userData.apply {
                        notificationsAreChecked = true
                    })
                    progressState.postValue(false)
                    notificationsState.postValue(canBeMoreItems(notificationsResultState) to notificationsResultState)
                }
            }
        }
    }

    private fun canBeMoreItems(notificationsResultState: NotificationsResultState) = (((notificationsResultState is NotificationsResultState.FirstResults) && notificationsResultState.results.size == perPage) ||
            ((notificationsResultState is NotificationsResultState.MoreResults) && notificationsResultState.results.size == perPage))

    fun getRecyclerItems(notificationSwitch: NotificationsUiModel.NotificationsSwitch, notificationItems: List<NotificationData>, progressItem: NotificationsUiModel.ProgressUiModel? = null, context: Context): ArrayList<NotificationsUiModel>{
        val adapterItems = ArrayList<NotificationsUiModel>()
        adapterItems.add(notificationSwitch)
        adapterItems.addAll(notificationItems.map { it.toUiModel(context) })
        if(progressItem != null)
            adapterItems.add(progressItem)
        return adapterItems
    }

    sealed class NotificationsResultState{
        data class FirstResults(val results: List<NotificationData>): NotificationsResultState()
        data class MoreResults(val results: List<NotificationData>) : NotificationsResultState()
        object NoResultsFound: NotificationsResultState()
        object NoMoreResultsFound: NotificationsResultState()
    }

}