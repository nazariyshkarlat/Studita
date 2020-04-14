package com.example.studita.di.data

import com.example.studita.data.cache.user_statistics.UserStatisticsCacheImpl
import com.example.studita.data.entity.mapper.UserStatisticsDataMapper
import com.example.studita.data.net.UserStatisticsService
import com.example.studita.data.repository.UserStatisticsRepositoryImpl
import com.example.studita.data.repository.datasource.user_statistics.CloudUserStatisticsJsonDataStore
import com.example.studita.data.repository.datasource.user_statistics.DiskUserStatisticsJsonDataStore
import com.example.studita.data.repository.datasource.user_statistics.UserStatisticsJsonDataStoreFactoryImpl
import com.example.studita.di.DI
import com.example.studita.di.DiskModule
import com.example.studita.di.NetworkModule
import com.example.studita.domain.interactor.user_statistics.UserStatisticsInteractor
import com.example.studita.domain.interactor.user_statistics.UserStatisticsInteractorImpl
import com.example.studita.domain.repository.UserStatisticsRepository

object UserStatisticsModule {

    private lateinit var config: DI.Config

    private var repository: UserStatisticsRepository? = null
    private var userStatisticsInteractor: UserStatisticsInteractor? = null

    fun initialize(configuration: DI.Config = DI.Config.RELEASE) {
        config = configuration
    }

    fun getUserStatisticsInteractorImpl(): UserStatisticsInteractor {
        if (config == DI.Config.RELEASE && userStatisticsInteractor == null)
            userStatisticsInteractor =
                makeUserStatisticsIntercator(
                    getUserStatisticsRepository()
                )
        return userStatisticsInteractor!!
    }

    private fun getUserStatisticsRepository(): UserStatisticsRepository {
        if (repository == null)
            repository = UserStatisticsRepositoryImpl(
                getUserStatisticsJsonDataStoreFactory(), UserStatisticsDataMapper(),
                NetworkModule.connectionManager
            )
        return repository!!
    }

    private fun makeUserStatisticsIntercator(repository: UserStatisticsRepository) =
        UserStatisticsInteractorImpl(
            repository
        )


    private fun getUserStatisticsJsonDataStoreFactory() =
        UserStatisticsJsonDataStoreFactoryImpl(
            getCloudUserStatisticsDataStore(),
            getDiskUserStatisticsDataStore()
        )

    private fun getCloudUserStatisticsDataStore() =
        CloudUserStatisticsJsonDataStore(
            NetworkModule.connectionManager,
            NetworkModule.getService(UserStatisticsService::class.java),
            getUserStatisticsCacheImpl()
        )

    private fun getDiskUserStatisticsDataStore() =
        DiskUserStatisticsJsonDataStore(getUserStatisticsCacheImpl())


    private fun getUserStatisticsCacheImpl() =
        UserStatisticsCacheImpl(DiskModule.sharedPreferences)


}