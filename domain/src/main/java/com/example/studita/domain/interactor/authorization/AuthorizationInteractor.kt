package com.example.studita.domain.interactor.authorization

import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.entity.authorization.AuthorizationRequestData
import com.example.studita.domain.entity.authorization.SignInWithGoogleRequestData
import com.example.studita.domain.interactor.LogInStatus
import com.example.studita.domain.interactor.SignInWithGoogleStatus
import com.example.studita.domain.interactor.SignUpStatus

interface AuthorizationInteractor{

    suspend fun signUp(authorizationRequestData: AuthorizationRequestData): SignUpStatus
    suspend fun logIn(authorizationRequestData: AuthorizationRequestData): LogInStatus
    suspend fun signInWithGoogle(signInWithGoogleRequestData: SignInWithGoogleRequestData): SignInWithGoogleStatus
    suspend fun signOut(userIdTokenData: UserIdTokenData)

}