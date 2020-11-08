package com.studita.di.data

import com.studita.data.cache.interesting.InterestingCacheImpl
import com.studita.data.net.InterestingListService
import com.studita.data.net.InterestingResultService
import com.studita.data.net.InterestingService
import com.studita.data.repository.InterestingRepositoryImpl
import com.studita.data.repository.datasource.interesting.CloudInterestingJsonDataStore
import com.studita.data.repository.datasource.interesting.DiskInterestingJsonDataStore
import com.studita.data.repository.datasource.interesting.InterestingJsonDataStoreFactoryImpl
import com.studita.data.repository.datasource.interesting.result.InterestingResultDataStoreFactory
import com.studita.data.repository.datasource.interesting.result.InterestingResultDataStoreFactoryImpl
import com.studita.data.repository.datasource.interesting.result.InterestingResultDataStoreImpl
import com.studita.di.CacheModule
import com.studita.di.DI
import com.studita.di.NetworkModule
import com.studita.domain.interactor.interesting.InterestingInteractor
import com.studita.domain.interactor.interesting.InterestingInteractorImpl
import com.studita.domain.repository.InterestingRepository
import com.studita.service.SyncInterestingLikesImpl

object InterestingModule {

    private lateinit var config: DI.Config

    private var repository: InterestingRepository? = null
    private var interestingInteractor: InterestingInteractor? = null


    fun initialize(configuration: DI.Config = DI.Config.RELEASE) {
        config = configuration
    }

    fun getInterestingInteractorImpl(): InterestingInteractor {
        if (config == DI.Config.RELEASE && interestingInteractor == null)
            interestingInteractor =
                makeInterestingInteractor(
                    getInterestingRepository()
                )
        return interestingInteractor!!
    }

    private fun getInterestingRepository(): InterestingRepository {
        if (repository == null)
            repository = InterestingRepositoryImpl(
                getInterestingJsonDataStoreFactory(),
                getInterestingResultDataStoreFactory(),
                NetworkModule.connectionManager
            )
        return repository!!
    }

    private fun makeInterestingInteractor(repository: InterestingRepository) =
        InterestingInteractorImpl(
            repository,
            SyncInterestingLikesImpl()
        )

    private fun getCloudInterestingDataStore() =
        CloudInterestingJsonDataStore(
            NetworkModule.connectionManager,
            NetworkModule.getService(InterestingService::class.java),
            NetworkModule.getService(InterestingListService::class.java)
        )

    private fun getDiskInterestingDataStore() =
        DiskInterestingJsonDataStore(getInterestingCacheImpl())

    private fun getInterestingCacheImpl() =
        InterestingCacheImpl(CacheModule.sharedPreferences)

    private fun getInterestingJsonDataStoreFactory() =
        InterestingJsonDataStoreFactoryImpl(
            getCloudInterestingDataStore(),
            getDiskInterestingDataStore()
        )

    private fun getInterestingResultDataStoreFactory() =
        InterestingResultDataStoreFactoryImpl(
            getInterestingResultDataStoreImpl()
        )

    private fun getInterestingResultDataStoreImpl() =
        InterestingResultDataStoreImpl(NetworkModule.connectionManager,
            NetworkModule.getService(InterestingResultService::class.java))
}