package com.example.studita.data.repository.datasource.notifications

import com.example.studita.data.entity.NotificationEntity
import com.example.studita.data.entity.UserIdToken

interface NotificationsDataStore {

    suspend fun tryGetNotifications(
        userIdToken: UserIdToken,
        perPage: Int,
        pageNumber: Int
    ): Pair<Int, List<NotificationEntity>?>

    suspend fun trySetNotificationsAreChecked(userIdToken: UserIdToken): Int
}