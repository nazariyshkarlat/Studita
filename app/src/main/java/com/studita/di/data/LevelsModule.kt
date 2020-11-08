package com.studita.di.data

import com.studita.data.cache.levels.LevelsCacheImpl
import com.studita.data.net.LevelsService
import com.studita.data.repository.LevelsRepositoryImpl
import com.studita.data.repository.datasource.levels.CloudLevelsJsonDataStore
import com.studita.data.repository.datasource.levels.DiskLevelsJsonDataStore
import com.studita.data.repository.datasource.levels.LevelsJsonDataStoreFactoryImpl
import com.studita.di.CacheModule
import com.studita.di.DI
import com.studita.di.NetworkModule
import com.studita.domain.interactor.levels.LevelsInteractor
import com.studita.domain.interactor.levels.LevelsInteractorImpl
import com.studita.domain.repository.LevelsRepository

object LevelsModule {

    private lateinit var config: DI.Config

    private var repository: LevelsRepository? = null
    private var levelsInteractor: LevelsInteractor? = null

    fun initialize(configuration: DI.Config = DI.Config.RELEASE) {
        config = configuration
    }

    fun getLevelsInteractorImpl(): LevelsInteractor {
        if (config == DI.Config.RELEASE && levelsInteractor == null)
            levelsInteractor =
                makeLevelsIntercator(
                    getLevelsRepository()
                )
        return levelsInteractor!!
    }

    private fun getLevelsRepository(): LevelsRepository {
        if (repository == null)
            repository = LevelsRepositoryImpl(
                getLevelsJsonDataStoreFactory(),
                NetworkModule.connectionManager
            )
        return repository!!
    }

    private fun makeLevelsIntercator(repository: LevelsRepository) =
        LevelsInteractorImpl(
            repository
        )

    private fun getCloudLevelsDataStore() =
        CloudLevelsJsonDataStore(
            NetworkModule.connectionManager,
            NetworkModule.getService(LevelsService::class.java)
        )

    private fun getDiskLevelsDataStore() =
        DiskLevelsJsonDataStore(getLevelsCacheImpl())

    private fun getLevelsCacheImpl() =
        LevelsCacheImpl(CacheModule.sharedPreferences)

    private fun getLevelsJsonDataStoreFactory() =
        LevelsJsonDataStoreFactoryImpl(
            getCloudLevelsDataStore(),
            getDiskLevelsDataStore()
        )
}