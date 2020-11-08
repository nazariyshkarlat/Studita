package com.studita.domain.repository

import com.studita.domain.entity.SignOutRequestData
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.entity.authorization.AuthorizationRequestData
import com.studita.domain.entity.authorization.LogInResponseData
import com.studita.domain.entity.authorization.SignInWithGoogleRequestData

interface AuthorizationRepository {

    suspend fun checkTokenIsCorrect(userIdTokenData: UserIdTokenData): Pair<Int, Boolean?>

    suspend fun signUp(authorizationRequestData: AuthorizationRequestData): Int

    suspend fun logIn(authorizationRequestData: AuthorizationRequestData): Pair<Int, LogInResponseData?>

    suspend fun signInWithGoogle(signInWithGoogleRequestData: SignInWithGoogleRequestData): Pair<Int, LogInResponseData?>

    suspend fun signOut(signOutRequestData: SignOutRequestData): Int
}