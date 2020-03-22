package com.example.studita.data.repository

import com.example.studita.data.entity.mapper.AuthorizationRequestMapper
import com.example.studita.data.entity.mapper.LogInResponseDataMapper
import com.example.studita.data.repository.datasource.authorization.AuthorizationDataStoreFactory
import com.example.studita.data.repository.datasource.authorization.AuthorizationDataStoreFactoryImpl
import com.example.studita.domain.entity.authorization.AuthorizationRequestData
import com.example.studita.domain.entity.authorization.LogInResponseData
import com.example.studita.domain.repository.AuthorizationRepository

class AuthorizationRepositoryImpl(private val authorizationDataStoreFactory: AuthorizationDataStoreFactoryImpl,
                                  private val logInResultDataMapper: LogInResponseDataMapper, private val authenticationRequestMapper: AuthorizationRequestMapper) : AuthorizationRepository {
    override suspend fun signUp(authorizationRequestData: AuthorizationRequestData): Int {
        return authorizationDataStoreFactory.create(AuthorizationDataStoreFactory.Priority.CLOUD).trySignUp(authenticationRequestMapper.map(authorizationRequestData))
    }

    override suspend fun logIn(authorizationRequestData: AuthorizationRequestData): Pair<Int, LogInResponseData?>{
        return authorizationDataStoreFactory.create(
            AuthorizationDataStoreFactory.Priority.CLOUD).tryLogIn(authenticationRequestMapper.map(authorizationRequestData)).let{
            it.first to it.second?.let{ second->
                logInResultDataMapper.map(second)
            }
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Pair<Int, LogInResponseData?> {
        return authorizationDataStoreFactory.create(
            AuthorizationDataStoreFactory.Priority.CLOUD).trySignInWithGoogle(idToken).let{
            it.first to it.second?.let{ second->
                logInResultDataMapper.map(second)
            }
        }
    }

}