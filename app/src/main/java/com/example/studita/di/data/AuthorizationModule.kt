package com.example.studita.di.data

import com.example.studita.di.DiskModule
import com.example.studita.data.cache.authentication.LogInCacheImpl
import com.example.studita.data.entity.mapper.AuthorizationRequestMapper
import com.example.studita.data.entity.mapper.LogInResponseDataMapper
import com.example.studita.data.net.AuthorizationService
import com.example.studita.data.repository.AuthorizationRepositoryImpl
import com.example.studita.data.repository.datasource.authorization.AuthorizationDataStoreFactoryImpl
import com.example.studita.data.repository.datasource.authorization.CloudAuthorizationDataStore
import com.example.studita.di.DI
import com.example.studita.di.NetworkModule
import com.example.studita.domain.interactor.authorization.AuthorizationInteractor
import com.example.studita.domain.interactor.authorization.AuthorizationInteractorImpl
import com.example.studita.domain.repository.AuthorizationRepository

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
                    getAuthorizationRepository()
                )
        return authorizationInteractor!!
    }

    private fun getAuthorizationRepository(): AuthorizationRepository {
        if (repository == null)
            repository = AuthorizationRepositoryImpl(
                getAuthorizationDataStoreFactory(),
                LogInResponseDataMapper(),
                AuthorizationRequestMapper()
            )
        return repository!!
    }

    private fun makeAuthorizationInteractor(repository: AuthorizationRepository) =
        AuthorizationInteractorImpl(
            repository
        )

    private fun getCloudAuthorizationDataStore() =
        CloudAuthorizationDataStore(
            NetworkModule.connectionManager,
            NetworkModule.getService(AuthorizationService::class.java),
            getLogInCacheImpl()
        )

    private fun getAuthorizationDataStoreFactory() =
        AuthorizationDataStoreFactoryImpl(
            getCloudAuthorizationDataStore()
        )

    private fun getLogInCacheImpl() =
        LogInCacheImpl(DiskModule.sharedPreferences)
}