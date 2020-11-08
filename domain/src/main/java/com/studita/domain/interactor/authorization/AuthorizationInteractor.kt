package com.studita.domain.interactor.authorization

import com.studita.domain.entity.SignOutRequestData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.entity.authorization.AuthorizationRequestData
import com.studita.domain.entity.authorization.SignInWithGoogleRequestData
import com.studita.domain.interactor.CheckTokenIsCorrectStatus
import com.studita.domain.interactor.LogInStatus
import com.studita.domain.interactor.SignInWithGoogleStatus
import com.studita.domain.interactor.SignUpStatus

interface AuthorizationInteractor {

    suspend fun checkTokenIsCorrect(
        userIdTokenData: UserIdTokenData,
        retryCount: Int = Int.MAX_VALUE
    ): CheckTokenIsCorrectStatus

    suspend fun signUp(
        authorizationRequestData: AuthorizationRequestData,
        retryCount: Int = 3
    ): SignUpStatus

    suspend fun logIn(
        authorizationRequestData: AuthorizationRequestData,
        retryCount: Int = 3
    ): LogInStatus

    suspend fun signInWithGoogle(
        signInWithGoogleRequestData: SignInWithGoogleRequestData,
        retryCount: Int = 3
    ): SignInWithGoogleStatus

    suspend fun signOut(signOutRequestData: SignOutRequestData, retryCount: Int = 3)
    suspend fun deleteUserData()

}