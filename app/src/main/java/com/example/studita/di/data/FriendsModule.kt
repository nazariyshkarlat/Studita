package com.example.studita.di.data

import com.example.studita.data.entity.mapper.FriendActionRequestEntityMapper
import com.example.studita.data.entity.mapper.FriendEntityMapper
import com.example.studita.data.entity.mapper.UserIdTokenMapper
import com.example.studita.data.net.FriendsService
import com.example.studita.data.repository.FriendsRepositoryImpl
import com.example.studita.data.repository.datasource.friends.FriendsDataStoreFactoryImpl
import com.example.studita.data.repository.datasource.friends.FriendsDataStoreImpl
import com.example.studita.di.DI
import com.example.studita.di.NetworkModule
import com.example.studita.domain.interactor.friends.FriendsInteractor
import com.example.studita.domain.interactor.friends.FriendsInteractorImpl
import com.example.studita.domain.repository.FriendsRepository

object FriendsModule {

    private lateinit var config: DI.Config

    private var repository: FriendsRepository? = null
    private var friendsInteractor: FriendsInteractor? = null

    fun initialize(configuration: DI.Config = DI.Config.RELEASE) {
        config = configuration
    }

    fun getFriendsInteractorImpl(): FriendsInteractor {
        if (config == DI.Config.RELEASE && friendsInteractor == null)
            friendsInteractor =
                    makeFriendsIntercator(
                            getFriendsRepository()
                    )
        return friendsInteractor!!
    }

    private fun getFriendsRepository(): FriendsRepository {
        if (repository == null)
            repository = FriendsRepositoryImpl(
                    getFriendsDataStoreFactory(),
                    UserIdTokenMapper(),
                    FriendEntityMapper(),
                    getFriendActionRequestEntityMapper()
            )
        return repository!!
    }

    private fun makeFriendsIntercator(repository: FriendsRepository) =
            FriendsInteractorImpl(
                    repository
            )


    private fun getFriendsDataStoreFactory() =
            FriendsDataStoreFactoryImpl(
                    getCloudFriendsDataStore())

    private fun getCloudFriendsDataStore() =
            FriendsDataStoreImpl(
                    NetworkModule.connectionManager,
                    NetworkModule.getService(FriendsService::class.java))

    private fun getFriendActionRequestEntityMapper() = FriendActionRequestEntityMapper(UserIdTokenMapper())
}