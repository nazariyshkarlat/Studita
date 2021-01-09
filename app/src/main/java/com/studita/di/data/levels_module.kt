package com.studita.di.data

import com.studita.data.cache.levels.LevelsCache
import com.studita.data.cache.levels.LevelsCacheImpl
import com.studita.data.net.LevelsService
import com.studita.data.repository.LevelsRepositoryImpl
import com.studita.data.repository.datasource.levels.CloudLevelsJsonDataStore
import com.studita.data.repository.datasource.levels.DiskLevelsJsonDataStore
import com.studita.data.repository.datasource.levels.LevelsJsonDataStoreFactory
import com.studita.data.repository.datasource.levels.LevelsJsonDataStoreFactoryImpl
import com.studita.di.DI
import com.studita.di.configModule
import com.studita.di.getService
import com.studita.domain.interactor.levels.LevelsInteractor
import com.studita.domain.interactor.levels.LevelsInteractorImpl
import com.studita.domain.repository.LevelsRepository
import org.koin.core.context.GlobalContext
import org.koin.dsl.bind

fun createLevelsModule(config: DI.Config) =  configModule(configuration = config){

    single {
        LevelsInteractorImpl(
            get()
        )
    } bind (LevelsInteractor::class)

    single{
        LevelsRepositoryImpl(
            get(),
            GlobalContext.get().get()
        )
    } bind (LevelsRepository::class)

    single {
        CloudLevelsJsonDataStore(
            GlobalContext.get().get(),
            getService(LevelsService::class.java)
        )
    }

    single {
        DiskLevelsJsonDataStore(get())
    }

    single {
        LevelsCacheImpl(GlobalContext.get().get())
    } bind (LevelsCache::class)

    single {
        LevelsJsonDataStoreFactoryImpl(
            get(),
            get(),
        )
    } bind (LevelsJsonDataStoreFactory::class)
}