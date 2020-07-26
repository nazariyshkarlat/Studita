package com.example.studita.data.repository.datasource.notifications

import android.app.Notification
import com.example.studita.data.entity.NotificationEntity
import com.example.studita.data.entity.UserIdToken
import com.example.studita.data.entity.UsersResponse

interface NotificationsDataStore {

    suspend fun tryGetNotifications(userIdToken: UserIdToken, perPage: Int, pageNumber: Int) : Pair<Int, List<NotificationEntity>?>

    suspend fun trySetNotificationsAreChecked(userIdToken: UserIdToken): Int
}