package com.example.studita.data.repository

import com.example.studita.data.entity.toBusinessEntity
import com.example.studita.data.entity.toRawEntity
import com.example.studita.data.repository.datasource.authorization.AuthorizationDataStoreFactoryImpl
import com.example.studita.domain.entity.SignOutRequestData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.entity.authorization.AuthorizationRequestData
import com.example.studita.domain.entity.authorization.LogInResponseData
import com.example.studita.domain.entity.authorization.SignInWithGoogleRequestData
import com.example.studita.domain.repository.AuthorizationRepository

class AuthorizationRepositoryImpl(private val authorizationDataStoreFactory: AuthorizationDataStoreFactoryImpl) :
    AuthorizationRepository {
    override suspend fun checkTokenIsCorrect(userIdTokenData: UserIdTokenData): Pair<Int, Boolean?> {
        return authorizationDataStoreFactory.create()
            .tryCheckTokenIsCorrect(userIdTokenData.toRawEntity())
    }

    override suspend fun signUp(authorizationRequestData: AuthorizationRequestData): Int {
        return authorizationDataStoreFactory.create()
            .trySignUp(authorizationRequestData.toRawEntity())
    }

    override suspend fun logIn(authorizationRequestData: AuthorizationRequestData): Pair<Int, LogInResponseData?> {
        return authorizationDataStoreFactory.create()
            .tryLogIn(authorizationRequestData.toRawEntity()).let {
            it.first to it.second?.toBusinessEntity()
        }
    }

    override suspend fun signInWithGoogle(signInWithGoogleRequestData: SignInWithGoogleRequestData): Pair<Int, LogInResponseData?> {
        return authorizationDataStoreFactory.create()
            .trySignInWithGoogle(signInWithGoogleRequestData.toRawEntity()).let {
            it.first to it.second?.toBusinessEntity()
        }
    }

    override suspend fun signOut(signOutRequestData: SignOutRequestData): Int {
        return authorizationDataStoreFactory.create().trySignOut(signOutRequestData.toRawEntity())
    }

}