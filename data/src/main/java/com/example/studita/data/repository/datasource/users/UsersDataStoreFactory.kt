package com.example.studita.data.repository.datasource.users

class UsersDataStoreFactoryImpl(
        private val usersDataStoreImpl: UsersDataStoreImpl
) : UsersDataStoreFactory {

    override fun create() =
        usersDataStoreImpl
}

interface UsersDataStoreFactory{

    fun create(): UsersDataStore

}