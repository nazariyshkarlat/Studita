package com.example.studita.domain.repository

import com.example.studita.domain.entity.authorization.AuthorizationRequestData
import com.example.studita.domain.entity.authorization.LogInResponseData

interface AuthorizationRepository {

    suspend fun signUp(authorizationRequestData: AuthorizationRequestData): Int

    suspend fun logIn(authorizationRequestData: AuthorizationRequestData): Pair<Int, LogInResponseData?>

    suspend fun signInWithGoogle(idToken: String) : Pair<Int, LogInResponseData?>
}