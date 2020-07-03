package com.example.studita.di.data

import com.example.studita.data.net.UsersService
import com.example.studita.data.repository.UsersRepositoryImpl
import com.example.studita.data.repository.datasource.users.UsersDataStoreFactoryImpl
import com.example.studita.data.repository.datasource.users.UsersDataStoreImpl
import com.example.studita.di.DI
import com.example.studita.di.NetworkModule
import com.example.studita.domain.interactor.users.UsersInteractor
import com.example.studita.domain.interactor.users.UsersInteractorImpl
import com.example.studita.domain.repository.UsersRepository
import com.example.studita.service.SyncFriendshipImpl

object UsersModule {

    private lateinit var config: DI.Config

    private var repository: UsersRepository? = null
    private var usersInteractor: UsersInteractor? = null

    fun initialize(configuration: DI.Config = DI.Config.RELEASE) {
        config = configuration
    }

    fun getUsersInteractorImpl(): UsersInteractor {
        if (config == DI.Config.RELEASE && usersInteractor == null)
            usersInteractor =
                    makeUsersIntercator(
                            getUsersRepository()
                    )
        return usersInteractor!!
    }

    private fun getUsersRepository(): UsersRepository {
        if (repository == null)
            repository = UsersRepositoryImpl(
                    getUsersDataStoreFactory()
            )
        return repository!!
    }

    private fun makeUsersIntercator(repository: UsersRepository) =
            UsersInteractorImpl(
                repository,
                syncFriendship = SyncFriendshipImpl()
            )


    private fun getUsersDataStoreFactory() =
            UsersDataStoreFactoryImpl(
                    getUsersDataStore())

    private fun getUsersDataStore() =
            UsersDataStoreImpl(
                    NetworkModule.connectionManager,
                    NetworkModule.getService(UsersService::class.java))

}