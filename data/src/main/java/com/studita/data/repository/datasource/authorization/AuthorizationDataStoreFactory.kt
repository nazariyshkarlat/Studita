package com.studita.data.repository.datasource.authorization

class AuthorizationDataStoreFactoryImpl(
    private val authorizationDataStore: AuthorizationDataStore
) : AuthorizationDataStoreFactory {

    override fun create() =
        authorizationDataStore
}

interface AuthorizationDataStoreFactory {

    fun create(): AuthorizationDataStore
}