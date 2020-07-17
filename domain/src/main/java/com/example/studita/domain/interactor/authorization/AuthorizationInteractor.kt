package com.example.studita.domain.interactor.authorization

import com.example.studita.domain.entity.SignOutRequestData
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.entity.authorization.AuthorizationRequestData
import com.example.studita.domain.entity.authorization.SignInWithGoogleRequestData
import com.example.studita.domain.interactor.CheckTokenIsCorrectStatus
import com.example.studita.domain.interactor.LogInStatus
import com.example.studita.domain.interactor.SignInWithGoogleStatus
import com.example.studita.domain.interactor.SignUpStatus

interface AuthorizationInteractor{

    suspend fun checkTokenIsCorrect(userIdTokenData: UserIdTokenData, retryCount: Int = 30) : CheckTokenIsCorrectStatus
    suspend fun signUp(authorizationRequestData: AuthorizationRequestData, retryCount: Int =30): SignUpStatus
    suspend fun logIn(authorizationRequestData: AuthorizationRequestData, retryCount: Int =30): LogInStatus
    suspend fun signInWithGoogle(signInWithGoogleRequestData: SignInWithGoogleRequestData, retryCount: Int =30): SignInWithGoogleStatus
    suspend fun signOut(signOutRequestData: SignOutRequestData, retryCount: Int=30)
    suspend fun deleteUserData()

}