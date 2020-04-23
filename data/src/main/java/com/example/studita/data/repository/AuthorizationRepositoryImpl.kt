package com.example.studita.data.repository

import com.example.studita.data.entity.mapper.AuthorizationRequestMapper
import com.example.studita.data.entity.mapper.LogInResponseDataMapper
import com.example.studita.data.entity.mapper.SignInWithGoogleRequestMapper
import com.example.studita.data.entity.mapper.UserDataEntityMapper
import com.example.studita.data.repository.datasource.authorization.AuthorizationDataStoreFactoryImpl
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.authorization.AuthorizationRequestData
import com.example.studita.domain.entity.authorization.LogInResponseData
import com.example.studita.domain.entity.authorization.SignInWithGoogleRequestData
import com.example.studita.domain.repository.AuthorizationRepository

class AuthorizationRepositoryImpl(private val authorizationDataStoreFactory: AuthorizationDataStoreFactoryImpl,
                                  private val logInResultDataMapper: LogInResponseDataMapper, private val authorizationRequestMapper: AuthorizationRequestMapper, private val signInWithGoogleRequestMapper: SignInWithGoogleRequestMapper
) : AuthorizationRepository {
    override suspend fun signUp(authorizationRequestData: AuthorizationRequestData): Int {
        return authorizationDataStoreFactory.create().trySignUp(authorizationRequestMapper.map(authorizationRequestData))
    }

    override suspend fun logIn(authorizationRequestData: AuthorizationRequestData): Pair<Int, LogInResponseData?>{
        return authorizationDataStoreFactory.create().tryLogIn(authorizationRequestMapper.map(authorizationRequestData)).let{
            it.first to it.second?.let{ second->
                logInResultDataMapper.map(second)
            }
        }
    }

    override suspend fun signInWithGoogle(signInWithGoogleRequestData: SignInWithGoogleRequestData): Pair<Int, LogInResponseData?> {
        return authorizationDataStoreFactory.create().trySignInWithGoogle(signInWithGoogleRequestMapper.map(signInWithGoogleRequestData)).let{
            it.first to it.second?.let{ second->
                logInResultDataMapper.map(second)
            }
        }
    }

}