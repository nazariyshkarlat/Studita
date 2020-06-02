package com.example.studita.di.data

import com.example.studita.service.SyncSubscribeEmailImpl
import com.example.studita.data.cache.subscribe_email.SubscribeEmailCacheImpl
import com.example.studita.data.entity.mapper.SubscribeEmailDataMapper
import com.example.studita.data.net.SubscribeEmailService
import com.example.studita.data.repository.SubscribeEmailRepositoryImpl
import com.example.studita.data.repository.datasource.subscribe_mail.SubscribeEmailDataStoreImpl
import com.example.studita.data.repository.datasource.subscribe_mail.SubscribeEmailDataStoreFactoryImpl
import com.example.studita.di.CacheModule
import com.example.studita.di.DI
import com.example.studita.di.NetworkModule
import com.example.studita.domain.interactor.subscribe_email.SubscribeEmailInteractor
import com.example.studita.domain.interactor.subscribe_email.SubscribeEmailInteractorImpl
import com.example.studita.domain.repository.SubscribeEmailRepository

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
                getSubscribeEmailCacheImpl(),
                SubscribeEmailDataMapper()
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
            getCloudSubscribeEmailDataStore())

    private fun getCloudSubscribeEmailDataStore() =
        SubscribeEmailDataStoreImpl(
            NetworkModule.connectionManager,
            NetworkModule.getService(SubscribeEmailService::class.java))

    private fun getSubscribeEmailCacheImpl() = SubscribeEmailCacheImpl(CacheModule.sharedPreferences)

}