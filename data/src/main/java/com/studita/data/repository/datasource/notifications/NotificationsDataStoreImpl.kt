package com.studita.data.repository.datasource.notifications

import com.studita.data.entity.NotificationEntity
import com.studita.data.entity.UserIdToken
import com.studita.data.net.NotificationsService
import com.studita.data.net.connection.ConnectionManager
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import kotlinx.coroutines.CancellationException

class NotificationsDataStoreImpl(
    private val connectionManager: ConnectionManager,
    private val notificationsService: NotificationsService
) :
    NotificationsDataStore {

    override suspend fun tryGetNotifications(
        userIdToken: UserIdToken,
        perPage: Int,
        pageNumber: Int
    ): Pair<Int, List<NotificationEntity>?> =
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
                e.printStackTrace()
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
                e.printStackTrace()
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }
}