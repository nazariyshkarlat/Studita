package com.example.studita.di

import com.example.studita.data.database.user_data.UserDataCacheImpl
import com.example.studita.data.entity.mapper.UserDataDataMapper
import com.example.studita.data.net.LevelsService
import com.example.studita.data.net.UserDataService
import com.example.studita.data.repository.UserDataRepositoryImpl
import com.example.studita.data.repository.datasource.levels.CloudLevelsJsonDataStore
import com.example.studita.data.repository.datasource.levels.DiskLevelsJsonDataStore
import com.example.studita.data.repository.datasource.user_data.*
import com.example.studita.domain.interactor.user_data.UserDataInteractor
import com.example.studita.domain.interactor.user_data.UserDataInteractorImpl
import com.example.studita.domain.repository.UserDataRepository

object UserDataModule {

    private lateinit var config: DI.Config

    private var repository: UserDataRepository? = null
    private var userDataInteractor: UserDataInteractor? = null

    fun initialize(configuration: DI.Config = DI.Config.RELEASE) {
        config = configuration
    }

    fun getUserDataInteractorImpl(): UserDataInteractor {
        if (config == DI.Config.RELEASE && userDataInteractor == null)
            userDataInteractor = makeUserDataIntercator(getUserDataRepository())
        return userDataInteractor!!
    }

    private fun getUserDataRepository(): UserDataRepository {
        if (repository == null)
            repository = UserDataRepositoryImpl(getUserDataDataStoreFactory(), UserDataDataMapper(), NetworkModule.connectionManager)
        return repository!!
    }

    private fun makeUserDataIntercator(repository: UserDataRepository) =
        UserDataInteractorImpl(
            repository
        )
    

    private fun getUserDataDataStoreFactory() =
        UserDataDataStoreFactoryImpl(
            getCloudUserDataDataStore(),
            getDiskUserDataDataStore()
        )

    private fun getCloudUserDataDataStore() =
        CloudUserDataJsonDataStore(
            NetworkModule.connectionManager,
            NetworkModule.getService(UserDataService::class.java),
            getUserDataCacheImpl()
        )

    private fun getDiskUserDataDataStore() =
        DiskUserDataJsonDataStore(getUserDataCacheImpl())


    private fun getUserDataCacheImpl() =
        UserDataCacheImpl(DiskModule.sharedPreferences)


}