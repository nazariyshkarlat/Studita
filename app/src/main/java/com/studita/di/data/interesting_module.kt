package com.studita.di.data

import com.studita.data.cache.interesting.InterestingCache
import com.studita.data.cache.interesting.InterestingCacheImpl
import com.studita.data.net.InterestingListService
import com.studita.data.net.InterestingResultService
import com.studita.data.net.InterestingService
import com.studita.data.repository.InterestingRepositoryImpl
import com.studita.data.repository.datasource.interesting.CloudInterestingJsonDataStore
import com.studita.data.repository.datasource.interesting.DiskInterestingJsonDataStore
import com.studita.data.repository.datasource.interesting.InterestingJsonDataStoreFactory
import com.studita.data.repository.datasource.interesting.InterestingJsonDataStoreFactoryImpl
import com.studita.data.repository.datasource.interesting.result.InterestingResultDataStore
import com.studita.data.repository.datasource.interesting.result.InterestingResultDataStoreFactory
import com.studita.data.repository.datasource.interesting.result.InterestingResultDataStoreFactoryImpl
import com.studita.data.repository.datasource.interesting.result.InterestingResultDataStoreImpl
import com.studita.di.DI
import com.studita.di.configModule
import com.studita.di.getService
import com.studita.domain.interactor.interesting.InterestingInteractor
import com.studita.domain.interactor.interesting.InterestingInteractorImpl
import com.studita.domain.repository.InterestingRepository
import com.studita.service.SyncInterestingLikesImpl
import org.koin.core.context.GlobalContext
import org.koin.dsl.bind

fun createInterestingModule(config: DI.Config) = configModule(configuration = config) {

    single {
        InterestingInteractorImpl(
            get(),
            SyncInterestingLikesImpl()
        )
    } bind (InterestingInteractor::class)

    single {
        InterestingRepositoryImpl(
            get(),
            get(),
            GlobalContext.get().get()
        )
    } bind (InterestingRepository::class)

    single {
        CloudInterestingJsonDataStore(
            GlobalContext.get().get(),
            getService(InterestingService::class.java),
            getService(InterestingListService::class.java)
        )
    }

    single {
        DiskInterestingJsonDataStore(get())
    }

    single {
        InterestingCacheImpl(GlobalContext.get().get())
    } bind (InterestingCache::class)

    single {
        InterestingJsonDataStoreFactoryImpl(
            get(),
            get()
        )
    } bind (InterestingJsonDataStoreFactory::class)

    single {
        InterestingResultDataStoreFactoryImpl(
            get()
        )
    } bind (InterestingResultDataStoreFactory::class)

    single {
        InterestingResultDataStoreImpl(
            GlobalContext.get().get(),
            getService(InterestingResultService::class.java)
        )
    } bind (InterestingResultDataStore::class)
}