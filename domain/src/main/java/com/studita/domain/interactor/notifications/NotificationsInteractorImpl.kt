package com.studita.domain.interactor.notifications

import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import com.studita.domain.interactor.EditPrivacySettingsStatus
import com.studita.domain.interactor.GetNotificationsStatus
import com.studita.domain.interactor.SetNotificationsAreCheckedStatus
import com.studita.domain.interactor.SubscribeEmailResultStatus
import com.studita.domain.repository.NotificationsRepository
import com.studita.domain.service.SyncNotificationsAreChecked
import kotlinx.coroutines.delay

class NotificationsInteractorImpl(private val repository: NotificationsRepository, private val syncNotificationsAreChecked: SyncNotificationsAreChecked) :
    NotificationsInteractor {

    val retryDelay = 1000L

    override suspend fun getNotifications(
        userIdTokenData: UserIdTokenData,
        perPage: Int,
        pageNumber: Int,
        retryCount: Int
    ): GetNotificationsStatus =
        try {
            val pair =
                repository.getNotifications(userIdTokenData, perPage, pageNumber)
            val code = pair.first
            val notifications = pair.second
            when (code) {
                200 -> if (notifications!!.isNotEmpty()) GetNotificationsStatus.Success(
                    ArrayList(notifications)
                ) else GetNotificationsStatus.NoNotificationsFound
                else -> GetNotificationsStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException) {
                        GetNotificationsStatus.NoConnection
                    } else
                        GetNotificationsStatus.ServiceUnavailable
                } else {
                    delay(retryDelay)
                    getNotifications(
                        userIdTokenData,
                        perPage,
                        pageNumber,
                        retryCount - 1
                    )
                }
            } else
                GetNotificationsStatus.Failure
        }

    override suspend fun setNotificationsAreChecked(
        userIdTokenData: UserIdTokenData,
        retryCount: Int
    ): SetNotificationsAreCheckedStatus =
        try {
            if (repository.setNotificationsAreChecked(userIdTokenData) == 200)
                SetNotificationsAreCheckedStatus.Success
            else
                SetNotificationsAreCheckedStatus.Failure
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if(e is NetworkConnectionException) {
                        syncNotificationsAreChecked.scheduleCheckNotifications( userIdTokenData)
                        SetNotificationsAreCheckedStatus.NoConnection
                    }else
                        SetNotificationsAreCheckedStatus.ServiceUnavailable
                }
                else {
                    delay(retryDelay)
                    setNotificationsAreChecked(userIdTokenData, retryCount - 1)
                }
            } else
                SetNotificationsAreCheckedStatus.Failure
        }
}