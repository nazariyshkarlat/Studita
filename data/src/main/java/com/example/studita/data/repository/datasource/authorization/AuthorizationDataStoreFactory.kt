package com.example.studita.data.repository.datasource.authorization

class AuthorizationDataStoreFactoryImpl(
    private val cloudAuthorizationDataStore: CloudAuthorizationDataStore
) : AuthorizationDataStoreFactory {

    override fun create(priority: AuthorizationDataStoreFactory.Priority) =
        if (priority == AuthorizationDataStoreFactory.Priority.CLOUD)
            cloudAuthorizationDataStore
        else
            cloudAuthorizationDataStore
}

interface AuthorizationDataStoreFactory{

    enum class Priority {
        CLOUD,
        CACHE
    }

    fun  create(priority: Priority): AuthorizationDataStore
}