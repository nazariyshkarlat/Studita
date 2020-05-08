package com.example.studita.di.data

import com.example.studita.di.CacheModule
import com.example.studita.data.cache.levels.LevelsCacheImpl
import com.example.studita.data.entity.mapper.LevelDataMapper
import com.example.studita.data.net.LevelsService
import com.example.studita.data.repository.LevelsRepositoryImpl
import com.example.studita.data.repository.datasource.levels.CloudLevelsJsonDataStore
import com.example.studita.data.repository.datasource.levels.DiskLevelsJsonDataStore
import com.example.studita.data.repository.datasource.levels.LevelsJsonDataStoreFactoryImpl
import com.example.studita.di.DI
import com.example.studita.di.NetworkModule
import com.example.studita.domain.interactor.levels.LevelsInteractor
import com.example.studita.domain.interactor.levels.LevelsInteractorImpl
import com.example.studita.domain.repository.LevelsRepository

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
                LevelDataMapper(),
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