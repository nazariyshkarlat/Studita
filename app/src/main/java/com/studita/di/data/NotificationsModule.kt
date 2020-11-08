package com.studita.di.data

import com.studita.data.net.NotificationsService
import com.studita.data.repository.NotificationsRepositoryImpl
import com.studita.data.repository.datasource.notifications.NotificationsDataStoreFactoryImpl
import com.studita.data.repository.datasource.notifications.NotificationsDataStoreImpl
import com.studita.di.DI
import com.studita.di.NetworkModule
import com.studita.domain.interactor.notifications.NotificationsInteractor
import com.studita.domain.interactor.notifications.NotificationsInteractorImpl
import com.studita.domain.repository.NotificationsRepository
import com.studita.service.SyncNotificationsAreCheckedImpl

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
            repository,
            SyncNotificationsAreCheckedImpl()
        )


    private fun getNotificationsDataStoreFactory() =
        NotificationsDataStoreFactoryImpl(
            getNotificationsDataStore()
        )

    private fun getNotificationsDataStore() =
        NotificationsDataStoreImpl(
            NetworkModule.connectionManager,
            NetworkModule.getService(NotificationsService::class.java)
        )

}