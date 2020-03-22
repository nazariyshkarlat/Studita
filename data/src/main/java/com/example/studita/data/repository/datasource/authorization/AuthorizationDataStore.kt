package com.example.studita.data.repository.datasource.authorization

import com.example.studita.data.entity.AuthorizationRequestEntity
import com.example.studita.data.entity.LogInResponseEntity

interface AuthorizationDataStore{

    suspend fun tryLogIn(authorizationRequestEntity: AuthorizationRequestEntity) : Pair<Int, LogInResponseEntity?>

    suspend fun trySignUp(authorizationRequestEntity: AuthorizationRequestEntity): Int

    suspend fun trySignInWithGoogle(idToken: String): Pair<Int, LogInResponseEntity?>
}