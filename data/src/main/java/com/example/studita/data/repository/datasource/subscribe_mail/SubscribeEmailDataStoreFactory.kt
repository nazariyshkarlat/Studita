package com.example.studita.data.repository.datasource.subscribe_mail

class SubscribeEmailDataStoreFactoryImpl(
    private val subscribeEmailDataStore: SubscribeEmailDataStore
) : SubscribeEmailDataStoreFactory {

    override fun create() =
        subscribeEmailDataStore
}

interface SubscribeEmailDataStoreFactory {

    fun create(): SubscribeEmailDataStore

}