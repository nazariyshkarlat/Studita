package com.example.studita.data.repository.subscribe_mail

import com.example.studita.data.repository.datasource.authorization.AuthorizationDataStore
import com.example.studita.data.repository.datasource.authorization.CloudAuthorizationDataStore

class SubscribeEmailDataStoreFactoryImpl(
    private val cloudSubscribeEmailDataStore: CloudSubscribeEmailDataStore
) : SubscribeEmailDataStoreFactory {

    override fun create(priority: SubscribeEmailDataStoreFactory.Priority) =
        if (priority == SubscribeEmailDataStoreFactory.Priority.CLOUD)
            cloudSubscribeEmailDataStore
        else
            cloudSubscribeEmailDataStore
}

interface SubscribeEmailDataStoreFactory{

    enum class Priority {
        CLOUD,
        CACHE
    }

    fun  create(priority: Priority): CloudSubscribeEmailDataStore
}