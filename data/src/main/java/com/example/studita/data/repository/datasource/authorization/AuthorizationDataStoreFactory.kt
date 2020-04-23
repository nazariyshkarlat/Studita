package com.example.studita.data.repository.datasource.authorization

class AuthorizationDataStoreFactoryImpl(
    private val authorizationDataStoreImpl: AuthorizationDataStoreImpl
) : AuthorizationDataStoreFactory {

    override fun create() =
        authorizationDataStoreImpl
}

interface AuthorizationDataStoreFactory{

    fun  create(): AuthorizationDataStore
}