package com.example.studita.domain.repository

import com.example.studita.domain.entity.SignOutRequestData
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.entity.authorization.AuthorizationRequestData
import com.example.studita.domain.entity.authorization.LogInResponseData
import com.example.studita.domain.entity.authorization.SignInWithGoogleRequestData

interface AuthorizationRepository {

    suspend fun signUp(authorizationRequestData: AuthorizationRequestData):  Int

    suspend fun logIn(authorizationRequestData: AuthorizationRequestData): Pair<Int, LogInResponseData?>

    suspend fun signInWithGoogle(signInWithGoogleRequestData: SignInWithGoogleRequestData) : Pair<Int, LogInResponseData?>

    suspend fun signOut(signOutRequestData: SignOutRequestData): Int
}