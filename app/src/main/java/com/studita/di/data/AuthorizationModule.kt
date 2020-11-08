package com.studita.di.data

import com.studita.data.cache.authentication.LogInCacheImpl
import com.studita.data.net.AuthorizationService
import com.studita.data.repository.AuthorizationRepositoryImpl
import com.studita.data.repository.datasource.authorization.AuthorizationDataStoreFactoryImpl
import com.studita.data.repository.datasource.authorization.AuthorizationDataStoreImpl
import com.studita.di.CacheModule
import com.studita.di.DI
import com.studita.di.NetworkModule
import com.studita.domain.interactor.authorization.AuthorizationInteractor
import com.studita.domain.interactor.authorization.AuthorizationInteractorImpl
import com.studita.domain.repository.AuthorizationRepository
import com.studita.domain.repository.UserDataRepository
import com.studita.service.SyncSignOutImpl

object AuthorizationModule {
    private lateinit var config: DI.Config

    private var repository: AuthorizationRepository? = null
    private var authorizationInteractor: AuthorizationInteractor? = null


    fun initialize(configuration: DI.Config = DI.Config.RELEASE) {
        config = configuration
    }

    fun getAuthorizationInteractorImpl(): AuthorizationInteractor {
        if (config == DI.Config.RELEASE && authorizationInteractor == null)
            authorizationInteractor =
                makeAuthorizationInteractor(
                    getAuthorizationRepository(),
                    UserDataModule.getUserDataRepository()
                )
        return authorizationInteractor!!
    }

    private fun getAuthorizationRepository(): AuthorizationRepository {
        if (repository == null)
            repository = AuthorizationRepositoryImpl(
                getAuthorizationDataStoreFactory()
            )
        return repository!!
    }

    private fun makeAuthorizationInteractor(
        authorizationRepository: AuthorizationRepository,
        userDataRepository: UserDataRepository
    ) =
        AuthorizationInteractorImpl(
            authorizationRepository,
            userDataRepository,
            SyncSignOutImpl()
        )

    private fun getCloudAuthorizationDataStore() =
        AuthorizationDataStoreImpl(
            NetworkModule.connectionManager,
            NetworkModule.getService(AuthorizationService::class.java),
            getLogInCacheImpl()
        )

    private fun getAuthorizationDataStoreFactory() =
        AuthorizationDataStoreFactoryImpl(
            getCloudAuthorizationDataStore()
        )

    private fun getLogInCacheImpl() =
        LogInCacheImpl(CacheModule.sharedPreferences)
}