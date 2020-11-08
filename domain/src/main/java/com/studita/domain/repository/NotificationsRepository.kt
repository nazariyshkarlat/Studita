package com.studita.domain.repository

import com.studita.domain.entity.NotificationData
import com.studita.domain.entity.UserIdTokenData

interface NotificationsRepository {

    suspend fun getNotifications(
        userIdTokenData: UserIdTokenData,
        perPage: Int,
        pageNumber: Int
    ): Pair<Int, List<NotificationData>?>

    suspend fun setNotificationsAreChecked(userIdTokenData: UserIdTokenData): Int
}