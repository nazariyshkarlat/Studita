package com.example.studita.data.repository.datasource.notifications

import android.app.Notification
import com.example.studita.data.entity.NotificationEntity
import com.example.studita.data.entity.UserIdToken
import com.example.studita.data.entity.UsersResponse
import com.example.studita.data.net.NotificationsService
import com.example.studita.data.net.UsersService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.repository.datasource.users.UsersDataStore
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import kotlinx.coroutines.CancellationException
import java.lang.Exception

class NotificationsDataStoreImpl(private val connectionManager: ConnectionManager, private val notificationsService: NotificationsService):
    NotificationsDataStore {

    override suspend fun tryGetNotifications(userIdToken: UserIdToken, perPage: Int, pageNumber: Int): Pair<Int, List<NotificationEntity>?> =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val result = notificationsService.getNotifications(
                    userIdToken,
                    perPage,
                    pageNumber
                )
                result.code() to result.body()
            } catch (e: Exception) {
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }

    override suspend fun trySetNotificationsAreChecked(userIdToken: UserIdToken): Int =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val result = notificationsService.setNotificationsAreChecked(userIdToken)
                result.code()
            } catch (e: Exception) {
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }
}