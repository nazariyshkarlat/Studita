package com.studita.di.data

import com.studita.data.cache.subscribe_email.SubscribeEmailCacheImpl
import com.studita.data.net.SubscribeEmailService
import com.studita.data.repository.SubscribeEmailRepositoryImpl
import com.studita.data.repository.datasource.subscribe_mail.SubscribeEmailDataStoreFactoryImpl
import com.studita.data.repository.datasource.subscribe_mail.SubscribeEmailDataStoreImpl
import com.studita.di.CacheModule
import com.studita.di.DI
import com.studita.di.NetworkModule
import com.studita.domain.interactor.subscribe_email.SubscribeEmailInteractor
import com.studita.domain.interactor.subscribe_email.SubscribeEmailInteractorImpl
import com.studita.domain.repository.SubscribeEmailRepository
import com.studita.service.SyncSubscribeEmailImpl

object SubscribeEmailModule {

    private lateinit var config: DI.Config

    private var repository: SubscribeEmailRepository? = null
    private var subscribeEmailInteractor: SubscribeEmailInteractor? = null

    fun initialize(configuration: DI.Config = DI.Config.RELEASE) {
        config = configuration
    }

    fun getSubscribeEmailInteractorImpl(): SubscribeEmailInteractor {
        if (config == DI.Config.RELEASE && subscribeEmailInteractor == null)
            subscribeEmailInteractor =
                makeSubscribeEmailIntercator(
                    getSubscribeEmailRepository()
                )
        return subscribeEmailInteractor!!
    }

    private fun getSubscribeEmailRepository(): SubscribeEmailRepository {
        if (repository == null)
            repository = SubscribeEmailRepositoryImpl(
                getSubscribeEmailDataStoreFactory(),
                getSubscribeEmailCacheImpl()
            )
        return repository!!
    }

    private fun makeSubscribeEmailIntercator(repository: SubscribeEmailRepository) =
        SubscribeEmailInteractorImpl(
            repository,
            SyncSubscribeEmailImpl()
        )


    private fun getSubscribeEmailDataStoreFactory() =
        SubscribeEmailDataStoreFactoryImpl(
            getCloudSubscribeEmailDataStore()
        )

    private fun getCloudSubscribeEmailDataStore() =
        SubscribeEmailDataStoreImpl(
            NetworkModule.connectionManager,
            NetworkModule.getService(SubscribeEmailService::class.java)
        )

    private fun getSubscribeEmailCacheImpl() =
        SubscribeEmailCacheImpl(CacheModule.sharedPreferences)

}