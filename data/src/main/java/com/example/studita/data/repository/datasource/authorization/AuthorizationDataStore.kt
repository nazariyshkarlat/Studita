package com.example.studita.data.repository.datasource.authorization

import com.example.studita.data.entity.*

interface AuthorizationDataStore{

    suspend fun tryLogIn(authorizationRequestEntity: AuthorizationRequestEntity) : Pair<Int, LogInResponseEntity?>

    suspend fun trySignUp(authorizationRequestEntity: AuthorizationRequestEntity): Int

    suspend fun trySignOut(userIdToken: UserIdToken): Int

    suspend fun trySignInWithGoogle(signInWithGoogleRequestEntity: SignInWithGoogleRequestEntity): Pair<Int, LogInResponseEntity?>
}