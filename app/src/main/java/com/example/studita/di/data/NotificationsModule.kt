package com.example.studita.di.data

import com.example.studita.data.net.NotificationsService
import com.example.studita.data.repository.NotificationsRepositoryImpl
import com.example.studita.data.repository.datasource.notifications.NotificationsDataStoreFactoryImpl
import com.example.studita.data.repository.datasource.notifications.NotificationsDataStoreImpl
import com.example.studita.di.DI
import com.example.studita.di.NetworkModule
import com.example.studita.domain.interactor.notifications.NotificationsInteractor
import com.example.studita.domain.interactor.notifications.NotificationsInteractorImpl
import com.example.studita.domain.repository.NotificationsRepository
import com.example.studita.service.SyncFriendshipImpl

object NotificationsModule {

    private lateinit var config: DI.Config

    private var repository: NotificationsRepository? = null
    private var notificationsInteractor: NotificationsInteractor? = null

    fun initialize(configuration: DI.Config = DI.Config.RELEASE) {
        config = configuration
    }

    fun getNotificationsInteractorImpl(): NotificationsInteractor {
        if (config == DI.Config.RELEASE && notificationsInteractor == null)
            notificationsInteractor =
                makeNotificationsIntercator(
                    getNotificationsRepository()
                )
        return notificationsInteractor!!
    }

    private fun getNotificationsRepository(): NotificationsRepository {
        if (repository == null)
            repository = NotificationsRepositoryImpl(
                getNotificationsDataStoreFactory()
            )
        return repository!!
    }

    private fun makeNotificationsIntercator(repository: NotificationsRepository) =
        NotificationsInteractorImpl(
            repository
        )


    private fun getNotificationsDataStoreFactory() =
        NotificationsDataStoreFactoryImpl(
            getNotificationsDataStore())

    private fun getNotificationsDataStore() =
        NotificationsDataStoreImpl(
            NetworkModule.connectionManager,
            NetworkModule.getService(NotificationsService::class.java))

}