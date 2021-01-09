package com.studita.di.data

import com.studita.data.database.StuditaDatabase
import com.studita.data.net.UserDataService
import com.studita.data.repository.UserDataRepositoryImpl
import com.studita.data.repository.datasource.user_data.CloudUserDataDataStore
import com.studita.data.repository.datasource.user_data.DiskUserDataDataStore
import com.studita.data.repository.datasource.user_data.UserDataDataStoreFactory
import com.studita.data.repository.datasource.user_data.UserDataDataStoreFactoryImpl
import com.studita.di.DI
import com.studita.di.getService
import com.studita.domain.interactor.user_data.UserDataInteractor
import com.studita.domain.interactor.user_data.UserDataInteractorImpl
import com.studita.domain.repository.UserDataRepository
import org.koin.core.context.GlobalContext
import org.koin.dsl.bind
import org.koin.dsl.module

fun createUserDataModule(config: DI.Config) = module {

    single {
        UserDataInteractorImpl(
            get()
        )
    } bind (UserDataInteractor::class)

    single{
        UserDataRepositoryImpl(
                get(),
                GlobalContext.get().get()
            )
    } bind (UserDataRepository::class)

    single {
        UserDataDataStoreFactoryImpl(
            get(),
            get()
        )
    } bind (UserDataDataStoreFactory::class)

    single {
        CloudUserDataDataStore(
            GlobalContext.get().get(),
            getService(UserDataService::class.java),
            get()
        )
    }

    single {
        DiskUserDataDataStore(get())
    }

    single{
        GlobalContext.get().get<StuditaDatabase>().getUserDataDao()
    }

}