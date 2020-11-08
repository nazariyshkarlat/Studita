package com.studita.data.repository

import com.studita.data.entity.toBusinessEntity
import com.studita.data.entity.toRawEntity
import com.studita.data.repository.datasource.notifications.NotificationsDataStoreFactory
import com.studita.domain.entity.NotificationData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.repository.NotificationsRepository

class NotificationsRepositoryImpl(private val notificationsDataStoreFactory: NotificationsDataStoreFactory) :
    NotificationsRepository {
    override suspend fun getNotifications(
        userIdTokenData: UserIdTokenData,
        perPage: Int,
        pageNumber: Int
    ): Pair<Int, List<NotificationData>?> {
        return with(
            notificationsDataStoreFactory.create()
                .tryGetNotifications(userIdTokenData.toRawEntity(), perPage, pageNumber)
        ) {
            this.first to this.second?.map { it.toBusinessEntity() }
        }
    }

    override suspend fun setNotificationsAreChecked(userIdTokenData: UserIdTokenData) =
        notificationsDataStoreFactory.create()
            .trySetNotificationsAreChecked(userIdTokenData.toRawEntity())

}