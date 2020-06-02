package com.example.studita.data.repository.datasource.friends

class FriendsDataStoreFactoryImpl(
        private val friendsDataStore: FriendsDataStore
) : FriendsDataStoreFactory {

    override fun create() =
            friendsDataStore
}

interface FriendsDataStoreFactory{

    fun create(): FriendsDataStore

}