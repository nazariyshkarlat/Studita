package com.studita.di.data

import com.studita.data.net.UserStatisticsService
import com.studita.data.net.UsersService
import com.studita.data.repository.UsersRepositoryImpl
import com.studita.data.repository.datasource.users.UsersDataStore
import com.studita.data.repository.datasource.users.UsersDataStoreFactory
import com.studita.data.repository.datasource.users.UsersDataStoreFactoryImpl
import com.studita.data.repository.datasource.users.UsersDataStoreImpl
import com.studita.di.DI
import com.studita.di.getService
import com.studita.domain.interactor.users.UsersInteractor
import com.studita.domain.interactor.users.UsersInteractorImpl
import com.studita.domain.repository.UsersRepository
import com.studita.service.SyncFriendshipImpl
import org.koin.core.context.GlobalContext
import org.koin.core.context.GlobalContext.get
import org.koin.core.parameter.parametersOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun createUsersModule(config: DI.Config) = module {

    single {
        UsersInteractorImpl(
            get(),
            syncFriendship = SyncFriendshipImpl()
        )
    } bind (UsersInteractor::class)

    single{
        UsersRepositoryImpl(
            get()
        )
    } bind (UsersRepository::class)

    single {
        UsersDataStoreFactoryImpl(
            get()
        )
    } bind (UsersDataStoreFactory::class)

    single {
        UsersDataStoreImpl(
            GlobalContext.get().get(),
            getService(UsersService::class.java)
        )
    } bind (UsersDataStore::class)

}