package com.studita.di.data

import com.studita.data.net.NotificationsService
import com.studita.data.repository.NotificationsRepositoryImpl
import com.studita.data.repository.datasource.notifications.NotificationsDataStore
import com.studita.data.repository.datasource.notifications.NotificationsDataStoreFactory
import com.studita.data.repository.datasource.notifications.NotificationsDataStoreFactoryImpl
import com.studita.data.repository.datasource.notifications.NotificationsDataStoreImpl
import com.studita.di.DI
import com.studita.di.configModule
import com.studita.di.getService
import com.studita.domain.interactor.notifications.NotificationsInteractor
import com.studita.domain.interactor.notifications.NotificationsInteractorImpl
import com.studita.domain.repository.NotificationsRepository
import com.studita.service.SyncNotificationsAreCheckedImpl
import org.koin.core.context.GlobalContext
import org.koin.core.context.GlobalContext.get
import org.koin.core.parameter.parametersOf
import org.koin.dsl.bind

fun createNotificationsModule(config: DI.Config) = configModule(configuration = config) {

    single {
        NotificationsInteractorImpl(
            get(),
            SyncNotificationsAreCheckedImpl()
        )
    } bind (NotificationsInteractor::class)

    single{
        NotificationsRepositoryImpl(
            get()
        )
    } bind (NotificationsRepository::class)

    single {
        NotificationsDataStoreFactoryImpl(
            get()
        )
    } bind (NotificationsDataStoreFactory::class)

    single {
        NotificationsDataStoreImpl(
            GlobalContext.get().get(),
            getService(NotificationsService::class.java)
        )
    } bind (NotificationsDataStore::class)

}