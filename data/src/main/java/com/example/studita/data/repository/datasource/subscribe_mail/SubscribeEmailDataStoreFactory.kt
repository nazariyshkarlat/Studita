package com.example.studita.data.repository.datasource.subscribe_mail

class SubscribeEmailDataStoreFactoryImpl(
    private val subscribeEmailDataStoreImpl: SubscribeEmailDataStoreImpl
) : SubscribeEmailDataStoreFactory {

    override fun create() =
        subscribeEmailDataStoreImpl
}

interface SubscribeEmailDataStoreFactory{

    fun create(): SubscribeEmailDataStoreImpl

}