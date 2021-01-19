package com.studita.di.data

import com.studita.data.cache.authentication.LogInCache
import com.studita.data.cache.authentication.LogInCacheImpl
import com.studita.data.net.AuthorizationService
import com.studita.data.repository.AuthorizationRepositoryImpl
import com.studita.data.repository.datasource.authorization.AuthorizationDataStore
import com.studita.data.repository.datasource.authorization.AuthorizationDataStoreFactory
import com.studita.data.repository.datasource.authorization.AuthorizationDataStoreFactoryImpl
import com.studita.data.repository.datasource.authorization.AuthorizationDataStoreImpl
import com.studita.di.DI
import com.studita.di.configModule
import com.studita.di.getService
import com.studita.domain.interactor.authorization.AuthorizationInteractor
import com.studita.domain.interactor.authorization.AuthorizationInteractorImpl
import com.studita.domain.repository.AuthorizationRepository
import com.studita.domain.repository.UserDataRepository
import com.studita.service.SyncSignOutImpl
import org.koin.core.context.GlobalContext
import org.koin.dsl.bind

fun createAuthorizationModule(config: DI.Config) = configModule(configuration = config){

    single {
        AuthorizationInteractorImpl(
            get(),
            get(),
            SyncSignOutImpl()
        )
    } bind (AuthorizationInteractor::class)

    single {
        AuthorizationRepositoryImpl(
            get()
        )
    } bind (AuthorizationRepository::class)

    single{
        AuthorizationDataStoreImpl(
            GlobalContext.get().get(),
            getService(AuthorizationService::class.java) ,
            get()
        )
    } bind (AuthorizationDataStore::class)

    single {
        AuthorizationDataStoreFactoryImpl(
            get()
        )
    } bind (AuthorizationDataStoreFactory::class)

    single {
        LogInCacheImpl(GlobalContext.get().get())
    } bind (LogInCache::class)
}
