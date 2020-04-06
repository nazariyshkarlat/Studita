package com.example.studita.di.data

import com.example.studita.data.database.interesting.InterestingCacheImpl
import com.example.studita.data.entity.mapper.InterestingDataMapper
import com.example.studita.data.net.InterestingService
import com.example.studita.data.repository.InterestingRepositoryImpl
import com.example.studita.data.repository.datasource.interesting.CloudInterestingJsonDataStore
import com.example.studita.data.repository.datasource.interesting.DiskInterestingJsonDataStore
import com.example.studita.data.repository.datasource.interesting.InterestingJsonDataStoreFactoryImpl
import com.example.studita.di.DI
import com.example.studita.di.DiskModule
import com.example.studita.di.NetworkModule
import com.example.studita.domain.interactor.interesting.InterestingInteractor
import com.example.studita.domain.interactor.interesting.InterestingInteractorImpl
import com.example.studita.domain.repository.InterestingRepository

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
                getInterestingJsonDataStoreFactory(), InterestingDataMapper(),
                NetworkModule.connectionManager
            )
        return repository!!
    }

    private fun makeInterestingInteractor(repository: InterestingRepository) =
        InterestingInteractorImpl(
            repository
        )

    private fun getCloudInterestingDataStore() =
        CloudInterestingJsonDataStore(
            NetworkModule.connectionManager,
            NetworkModule.getService(InterestingService::class.java),
            getInterestingCacheImpl()
        )

    private fun getDiskInterestingDataStore() =
        DiskInterestingJsonDataStore(getInterestingCacheImpl())

    private fun getInterestingCacheImpl() =
        InterestingCacheImpl(DiskModule.sharedPreferences)

    private fun getInterestingJsonDataStoreFactory() =
        InterestingJsonDataStoreFactoryImpl(
            getCloudInterestingDataStore(),
            getDiskInterestingDataStore()
        )
}