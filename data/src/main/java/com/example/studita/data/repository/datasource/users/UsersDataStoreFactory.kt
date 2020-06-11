package com.example.studita.data.repository.datasource.users

class UsersDataStoreFactoryImpl(
        private val usersDataStore: UsersDataStore
) : UsersDataStoreFactory {

    override fun create() =
            usersDataStore
}

interface UsersDataStoreFactory{

    fun create(): UsersDataStore

}