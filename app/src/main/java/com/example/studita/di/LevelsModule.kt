package com.example.studita.di

import com.example.studita.data.entity.mapper.LevelDataMapper
import com.example.studita.data.net.LevelsService
import com.example.studita.data.repository.LevelsRepositoryImpl
import com.example.studita.data.repository.datasource.levels.CloudLevelsDataStore
import com.example.studita.data.repository.datasource.levels.LevelsDataStoreFactoryImpl
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
            levelsInteractor = makeLevelsIntercator(getLevelsRepository())
        return levelsInteractor!!
    }

    private fun getLevelsRepository(): LevelsRepository {
        if (repository == null)
            repository = LevelsRepositoryImpl(getLevelsDataStoreFactory(), LevelDataMapper())
        return repository!!
    }

    private fun makeLevelsIntercator(repository: LevelsRepository) =
        LevelsInteractorImpl(
            repository
        )

    private fun getCloudLevelsDataStore() =
        CloudLevelsDataStore(
            NetworkModule.connectionManager,
            NetworkModule.getService(LevelsService::class.java)
        )

    private fun getLevelsDataStoreFactory() =
        LevelsDataStoreFactoryImpl(
            getCloudLevelsDataStore()
        )
}