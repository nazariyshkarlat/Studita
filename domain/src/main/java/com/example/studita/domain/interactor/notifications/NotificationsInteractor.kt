package com.example.studita.domain.interactor.notifications

import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.GetNotificationsStatus
import com.example.studita.domain.interactor.SetNotificationsAreCheckedStatus

interface NotificationsInteractor {
    suspend fun getNotifications(
        userIdTokenData: UserIdTokenData,
        perPage: Int,
        pageNumber: Int,
        retryCount: Int = 30
    ): GetNotificationsStatus

    suspend fun setNotificationsAreChecked(
        userIdTokenData: UserIdTokenData,
        retryCount: Int = 30
    ): SetNotificationsAreCheckedStatus
}