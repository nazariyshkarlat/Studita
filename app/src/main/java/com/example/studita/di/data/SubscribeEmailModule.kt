package com.example.studita.di.data

import com.example.studita.data.net.SubscribeEmailService
import com.example.studita.data.repository.SubscribeEmailRepositoryImpl
import com.example.studita.data.repository.subscribe_mail.CloudSubscribeEmailDataStore
import com.example.studita.data.repository.subscribe_mail.SubscribeEmailDataStoreFactoryImpl
import com.example.studita.di.DI
import com.example.studita.di.DiskModule
import com.example.studita.di.NetworkModule
import com.example.studita.domain.interactor.subscribe_email.SubscribeEmailInteractor
import com.example.studita.domain.interactor.subscribe_email.SubscribeEmailInteractorImpl
import com.example.studita.domain.repository.SubscribeEmailRepository

object SubscribeEmailModule {

    private lateinit var config: DI.Config

    private var repository: SubscribeEmailRepository? = null
    private var userDataInteractor: SubscribeEmailInteractor? = null

    fun initialize(configuration: DI.Config = DI.Config.RELEASE) {
        config = configuration
    }

    fun getSubscribeEmailInteractorImpl(): SubscribeEmailInteractor {
        if (config == DI.Config.RELEASE && userDataInteractor == null)
            userDataInteractor =
                makeSubscribeEmailIntercator(
                    getSubscribeEmailRepository()
                )
        return userDataInteractor!!
    }

    private fun getSubscribeEmailRepository(): SubscribeEmailRepository {
        if (repository == null)
            repository = SubscribeEmailRepositoryImpl(
                getSubscribeEmailDataStoreFactory()
            )
        return repository!!
    }

    private fun makeSubscribeEmailIntercator(repository: SubscribeEmailRepository) =
        SubscribeEmailInteractorImpl(
            repository
        )


    private fun getSubscribeEmailDataStoreFactory() =
        SubscribeEmailDataStoreFactoryImpl(
            getCloudSubscribeEmailDataStore())

    private fun getCloudSubscribeEmailDataStore() =
        CloudSubscribeEmailDataStore(
            NetworkModule.connectionManager,
            NetworkModule.getService(SubscribeEmailService::class.java))


}