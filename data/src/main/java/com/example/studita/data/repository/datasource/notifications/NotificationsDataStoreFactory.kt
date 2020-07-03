package com.example.studita.data.repository.datasource.notifications

import com.example.studita.data.repository.datasource.users.UsersDataStore

class NotificationsDataStoreFactoryImpl(
    private val notificationsDataStoreImpl: NotificationsDataStoreImpl
) : NotificationsDataStoreFactory {

    override fun create() = notificationsDataStoreImpl
}

interface NotificationsDataStoreFactory{

    fun create(): NotificationsDataStore

}