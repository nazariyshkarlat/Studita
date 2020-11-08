package com.studita.domain.interactor.notifications

import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.interactor.GetNotificationsStatus
import com.studita.domain.interactor.SetNotificationsAreCheckedStatus

interface NotificationsInteractor {
    suspend fun getNotifications(
        userIdTokenData: UserIdTokenData,
        perPage: Int,
        pageNumber: Int,
        retryCount: Int = 3
    ): GetNotificationsStatus

    suspend fun setNotificationsAreChecked(
        userIdTokenData: UserIdTokenData,
        retryCount: Int = 3
    ): SetNotificationsAreCheckedStatus
}