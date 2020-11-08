package com.studita.di.data

import com.studita.data.net.UserDataService
import com.studita.data.repository.UserDataRepositoryImpl
import com.studita.data.repository.datasource.user_data.CloudUserDataDataStore
import com.studita.data.repository.datasource.user_data.DiskUserDataDataStore
import com.studita.data.repository.datasource.user_data.UserDataJsonDataStoreFactoryImpl
import com.studita.di.DI
import com.studita.di.DatabaseModule
import com.studita.di.NetworkModule
import com.studita.domain.interactor.user_data.UserDataInteractor
import com.studita.domain.interactor.user_data.UserDataInteractorImpl
import com.studita.domain.repository.UserDataRepository

object UserDataModule {

    private lateinit var config: DI.Config

    private var repository: UserDataRepository? = null
    private var userDataInteractor: UserDataInteractor? = null

    fun initialize(configuration: DI.Config = DI.Config.RELEASE) {
        config = configuration
    }

    fun getUserDataInteractorImpl(): UserDataInteractor {
        if (config == DI.Config.RELEASE && userDataInteractor == null)
            userDataInteractor =
                makeUserDataIntercator(
                    getUserDataRepository()
                )
        return userDataInteractor!!
    }

    internal fun getUserDataRepository(): UserDataRepository {
        if (repository == null)
            repository = UserDataRepositoryImpl(
                getUserDataJsonDataStoreFactory(),
                NetworkModule.connectionManager
            )
        return repository!!
    }

    private fun makeUserDataIntercator(repository: UserDataRepository) =
        UserDataInteractorImpl(
            repository
        )


    private fun getUserDataJsonDataStoreFactory() =
        UserDataJsonDataStoreFactoryImpl(
            getCloudUserDataDataStore(),
            getDiskUserDataDataStore()
        )

    private fun getCloudUserDataDataStore() =
        CloudUserDataDataStore(
            NetworkModule.connectionManager,
            NetworkModule.getService(UserDataService::class.java),
            getUserDataDao()
        )

    private fun getDiskUserDataDataStore() =
        DiskUserDataDataStore(getUserDataDao())

    private fun getUserDataDao() = DatabaseModule.studitaDatabase.getUserDataDao()

}