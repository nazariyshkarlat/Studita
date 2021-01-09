package com.studita.di.data

import com.studita.data.database.StuditaDatabase
import com.studita.data.net.UserStatisticsService
import com.studita.data.repository.UserStatisticsRepositoryImpl
import com.studita.data.repository.datasource.user_statistics.CloudUserStatisticsJsonDataStore
import com.studita.data.repository.datasource.user_statistics.DiskUserStatisticsRecordsDataStore
import com.studita.data.repository.datasource.user_statistics.UserStatisticsJsonDataStoreFactory
import com.studita.data.repository.datasource.user_statistics.UserStatisticsJsonDataStoreFactoryImpl
import com.studita.di.DI
import com.studita.di.configModule
import com.studita.di.getService
import com.studita.domain.interactor.user_statistics.UserStatisticsInteractor
import com.studita.domain.interactor.user_statistics.UserStatisticsInteractorImpl
import com.studita.domain.repository.UserStatisticsRepository
import org.koin.core.context.GlobalContext
import org.koin.dsl.bind
import org.koin.dsl.module


fun createUserStatisticsModule(config: DI.Config) = configModule(configuration = config){

    single {
        UserStatisticsInteractorImpl(
            get()
        )
    } bind(UserStatisticsInteractor::class)

    single {
        UserStatisticsRepositoryImpl(
            get(),
            get(),
            GlobalContext.get().get()
            )
    }bind(UserStatisticsRepository::class)

    single {
        UserStatisticsJsonDataStoreFactoryImpl(
           get()
        )
    }bind(UserStatisticsJsonDataStoreFactory::class)

    single {
        CloudUserStatisticsJsonDataStore(
            GlobalContext.get().get(),
            getService(UserStatisticsService::class.java)
        )
    }

    single {
        DiskUserStatisticsRecordsDataStore(get())
    }

    single {
        GlobalContext.get().get<StuditaDatabase>().getUserStatisticsDao()
    }

}