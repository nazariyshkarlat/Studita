package com.studita.data.repository.datasource.notifications

import com.studita.data.entity.NotificationEntity
import com.studita.data.entity.UserIdToken

interface NotificationsDataStore {

    suspend fun tryGetNotifications(
        userIdToken: UserIdToken,
        perPage: Int,
        pageNumber: Int
    ): Pair<Int, List<NotificationEntity>?>

    suspend fun trySetNotificationsAreChecked(userIdToken: UserIdToken): Int
}