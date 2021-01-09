package com.studita.di.data

import com.studita.data.net.PrivacySettingsService
import com.studita.data.net.UserStatisticsService
import com.studita.data.repository.PrivacySettingsRepositoryImpl
import com.studita.data.repository.datasource.privacy_settings.PrivacySettingsDataStore
import com.studita.data.repository.datasource.privacy_settings.PrivacySettingsDataStoreFactory
import com.studita.data.repository.datasource.privacy_settings.PrivacySettingsDataStoreFactoryImpl
import com.studita.data.repository.datasource.privacy_settings.PrivacySettingsDataStoreImpl
import com.studita.di.DI
import com.studita.di.configModule
import com.studita.di.getService
import com.studita.domain.interactor.privacy_settings.PrivacySettingsInteractor
import com.studita.domain.interactor.privacy_settings.PrivacySettingsInteractorImpl
import com.studita.domain.repository.PrivacySettingsRepository
import com.studita.service.SyncPrivacySettingsImpl
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.parametersOf
import org.koin.dsl.bind

fun createPrivacySettingsModule(config: DI.Config) = configModule(configuration = config) {

    single {
        PrivacySettingsInteractorImpl(
            get(),
            SyncPrivacySettingsImpl()
        )
    } bind (PrivacySettingsInteractor::class)

    single {
        PrivacySettingsRepositoryImpl(
                get()
            )
    } bind (PrivacySettingsRepository::class)

    single {
        PrivacySettingsDataStoreFactoryImpl(
            get()
        )
    } bind (PrivacySettingsDataStoreFactory::class)

    single {
        PrivacySettingsDataStoreImpl(
            GlobalContext.get().get(),
            getService(PrivacySettingsService::class.java)
        )
    } bind (PrivacySettingsDataStore::class)
}