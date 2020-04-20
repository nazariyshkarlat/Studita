package com.example.studita.di.data

import com.example.studita.data.entity.mapper.UserStatisticsDataMapper
import com.example.studita.data.net.UserDataService
import com.example.studita.data.net.UserStatisticsService
import com.example.studita.data.repository.UserStatisticsRepositoryImpl
import com.example.studita.data.repository.datasource.user_data.CloudUserDataDataStore
import com.example.studita.data.repository.datasource.user_data.DiskUserDataDataStore
import com.example.studita.data.repository.datasource.user_data.UserDataJsonDataStoreFactoryImpl
import com.example.studita.data.repository.datasource.user_statistics.CloudUserStatisticsDataStore
import com.example.studita.data.repository.datasource.user_statistics.DiskUserStatisticsDataStore
import com.example.studita.data.repository.datasource.user_statistics.UserStatisticsDataStoreFactoryImpl
import com.example.studita.di.DI
import com.example.studita.di.DatabaseModule
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
                getUserStatisticsDataStoreFactory(), UserStatisticsDataMapper(),
                NetworkModule.connectionManager
            )
        return repository!!
    }

    private fun makeUserStatisticsIntercator(repository: UserStatisticsRepository) =
        UserStatisticsInteractorImpl(
            repository
        )


    private fun getUserStatisticsDataStoreFactory() =
        UserStatisticsDataStoreFactoryImpl(
            getCloudUserStatisticsDataStore(),
            getDiskUserStatisticsDataStore()
        )

    private fun getCloudUserStatisticsDataStore() =
        CloudUserStatisticsDataStore(
            NetworkModule.connectionManager,
            NetworkModule.getService(UserStatisticsService::class.java),
            getUserStatisticsDao()
        )

    private fun getDiskUserStatisticsDataStore() =
        DiskUserStatisticsDataStore(getUserStatisticsDao())

    private fun getUserStatisticsDao() = DatabaseModule.studitaDatabase!!.getUserStatisticsDao()


}