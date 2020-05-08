package com.example.studita.di.data

import com.example.studita.di.CacheModule
import com.example.studita.data.cache.authentication.LogInCacheImpl
import com.example.studita.data.entity.mapper.*
import com.example.studita.data.net.AuthorizationService
import com.example.studita.data.repository.AuthorizationRepositoryImpl
import com.example.studita.data.repository.UserDataRepositoryImpl
import com.example.studita.data.repository.datasource.authorization.AuthorizationDataStoreFactoryImpl
import com.example.studita.data.repository.datasource.authorization.AuthorizationDataStoreImpl
import com.example.studita.di.DI
import com.example.studita.di.NetworkModule
import com.example.studita.domain.interactor.authorization.AuthorizationInteractor
import com.example.studita.domain.interactor.authorization.AuthorizationInteractorImpl
import com.example.studita.domain.repository.AuthorizationRepository
import com.example.studita.domain.repository.UserDataRepository

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
                getAuthorizationDataStoreFactory(),
                LogInResponseDataMapper(UserDataDataMapper()),
                getAuthorizationRequestMapper(),
                getSignInWithGoogleRequestMapper()
            )
        return repository!!
    }

    private fun makeAuthorizationInteractor(authorizationRepository: AuthorizationRepository, userDataRepository: UserDataRepository) =
        AuthorizationInteractorImpl(
            authorizationRepository
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

    private fun getSignInWithGoogleRequestMapper() = SignInWithGoogleRequestMapper(
        UserDataEntityMapper(),
        UserStatisticsRowEntityMapper()
    )

    private fun getAuthorizationRequestMapper() =  AuthorizationRequestMapper(UserDataEntityMapper(), UserStatisticsRowEntityMapper())
}