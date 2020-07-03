package com.example.studita.data.repository

import com.example.studita.data.entity.toBusinessEntity
import com.example.studita.data.entity.toRawEntity
import com.example.studita.data.repository.datasource.notifications.NotificationsDataStore
import com.example.studita.data.repository.datasource.notifications.NotificationsDataStoreFactory
import com.example.studita.domain.entity.NotificationData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.repository.NotificationsRepository

class NotificationsRepositoryImpl(private val notificationsDataStoreFactory: NotificationsDataStoreFactory) : NotificationsRepository{
    override suspend fun getNotifications(
        userIdTokenData: UserIdTokenData,
        perPage: Int,
        pageNumber: Int
    ): Pair<Int, List<NotificationData>?> {
        return with(notificationsDataStoreFactory.create().tryGetNotifications(userIdTokenData.toRawEntity(), perPage, pageNumber)){
            this.first to this.second?.map { it.toBusinessEntity() }
        }
    }

}