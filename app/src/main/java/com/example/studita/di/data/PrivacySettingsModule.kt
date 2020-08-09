package com.example.studita.di.data

import com.example.studita.data.net.PrivacySettingsService
import com.example.studita.data.repository.PrivacySettingsRepositoryImpl
import com.example.studita.data.repository.datasource.privacy_settings.PrivacySettingsDataStoreFactoryImpl
import com.example.studita.data.repository.datasource.privacy_settings.PrivacySettingsDataStoreImpl
import com.example.studita.di.DI
import com.example.studita.di.NetworkModule
import com.example.studita.domain.interactor.privacy_settings.PrivacySettingsInteractor
import com.example.studita.domain.interactor.privacy_settings.PrivacySettingsInteractorImpl
import com.example.studita.domain.repository.PrivacySettingsRepository
import com.example.studita.service.SyncPrivacySettingsImpl

object PrivacySettingsModule {

    private lateinit var config: DI.Config

    private var repository: PrivacySettingsRepository? = null
    private var privacySettingsInteractor: PrivacySettingsInteractor? = null

    fun initialize(configuration: DI.Config = DI.Config.RELEASE) {
        config = configuration
    }

    fun getPrivacySettingsInteractorImpl(): PrivacySettingsInteractor {
        if (config == DI.Config.RELEASE && privacySettingsInteractor == null)
            privacySettingsInteractor =
                makePrivacySettingsIntercator(
                    getPrivacySettingsRepository()
                )
        return privacySettingsInteractor!!
    }

    private fun getPrivacySettingsRepository(): PrivacySettingsRepository {
        if (repository == null)
            repository = PrivacySettingsRepositoryImpl(
                getPrivacySettingsDataStoreFactory()
            )
        return repository!!
    }

    private fun makePrivacySettingsIntercator(repository: PrivacySettingsRepository) =
        PrivacySettingsInteractorImpl(
            repository,
            SyncPrivacySettingsImpl()
        )


    private fun getPrivacySettingsDataStoreFactory() =
        PrivacySettingsDataStoreFactoryImpl(
            getCloudPrivacySettingsDataStore()
        )

    private fun getCloudPrivacySettingsDataStore() =
        PrivacySettingsDataStoreImpl(
            NetworkModule.connectionManager,
            NetworkModule.getService(PrivacySettingsService::class.java)
        )
}