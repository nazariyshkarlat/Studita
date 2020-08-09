package com.example.studita.data.repository.datasource.notifications

class NotificationsDataStoreFactoryImpl(
    private val notificationsDataStoreImpl: NotificationsDataStoreImpl
) : NotificationsDataStoreFactory {

    override fun create() = notificationsDataStoreImpl
}

interface NotificationsDataStoreFactory {

    fun create(): NotificationsDataStore

}