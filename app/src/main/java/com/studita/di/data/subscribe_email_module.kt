package com.studita.di.data

import com.studita.data.cache.subscribe_email.SubscribeEmailCache
import com.studita.data.cache.subscribe_email.SubscribeEmailCacheImpl
import com.studita.data.database.StuditaDatabase
import com.studita.data.net.SubscribeEmailService
import com.studita.data.repository.SubscribeEmailRepositoryImpl
import com.studita.data.repository.datasource.subscribe_mail.SubscribeEmailDataStore
import com.studita.data.repository.datasource.subscribe_mail.SubscribeEmailDataStoreFactory
import com.studita.data.repository.datasource.subscribe_mail.SubscribeEmailDataStoreFactoryImpl
import com.studita.data.repository.datasource.subscribe_mail.SubscribeEmailDataStoreImpl
import com.studita.di.DI
import com.studita.di.configModule
import com.studita.di.getService
import com.studita.domain.interactor.subscribe_email.SubscribeEmailInteractor
import com.studita.domain.interactor.subscribe_email.SubscribeEmailInteractorImpl
import com.studita.domain.repository.SubscribeEmailRepository
import com.studita.service.SyncSubscribeEmailImpl
import org.koin.core.context.GlobalContext
import org.koin.dsl.bind
import org.koin.dsl.module

fun createSubscribeEmailModule(config: DI.Config) = configModule(configuration = config){

    single {
        SubscribeEmailInteractorImpl(
            get(),
            SyncSubscribeEmailImpl()
        )
    } bind (SubscribeEmailInteractor::class)

    single {
        SubscribeEmailRepositoryImpl(
            get(),
            get()
        )
    } bind (SubscribeEmailRepository::class)

    single {
        SubscribeEmailDataStoreFactoryImpl(
            get()
        )
    } bind (SubscribeEmailDataStoreFactory::class)

    single {
        SubscribeEmailDataStoreImpl(
            GlobalContext.get().get(),
            getService(SubscribeEmailService::class.java)
        )
    } bind (SubscribeEmailDataStore::class)

    single {
        SubscribeEmailCacheImpl(GlobalContext.get().get())
    } bind (SubscribeEmailCache::class)

}