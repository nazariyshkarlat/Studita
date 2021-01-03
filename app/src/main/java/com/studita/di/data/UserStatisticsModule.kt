package com.studita.di.data

import com.studita.data.net.UserStatisticsService
import com.studita.data.repository.UserStatisticsRepositoryImpl
import com.studita.data.repository.datasource.user_statistics.CloudUserStatisticsJsonDataStore
import com.studita.data.repository.datasource.user_statistics.DiskUserStatisticsRecordsDataStore
import com.studita.data.repository.datasource.user_statistics.UserStatisticsJsonDataStoreFactoryImpl
import com.studita.di.DI
import com.studita.di.DatabaseModule
import com.studita.di.NetworkModule
import com.studita.domain.interactor.user_statistics.UserStatisticsInteractor
import com.studita.domain.interactor.user_statistics.UserStatisticsInteractorImpl
import com.studita.domain.repository.UserStatisticsRepository

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
                getUserStatisticsJsonDataStoreFactory(),
                getDiskUserStatisticsDataStore(),
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
            getCloudUserStatisticsDataStore()
        )

    private fun getCloudUserStatisticsDataStore() =
        CloudUserStatisticsJsonDataStore(
            NetworkModule.connectionManager,
            NetworkModule.getService(UserStatisticsService::class.java)
        )

    private fun getDiskUserStatisticsDataStore() =
        DiskUserStatisticsRecordsDataStore(getUserStatisticsDao())

    private fun getUserStatisticsDao() = DatabaseModule.studitaDatabase.getUserStatisticsDao()

}