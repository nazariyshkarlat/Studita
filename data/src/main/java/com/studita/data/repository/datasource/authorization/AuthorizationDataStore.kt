package com.studita.data.repository.datasource.authorization

import com.studita.data.entity.*

interface AuthorizationDataStore {

    suspend fun tryCheckTokenIsCorrect(userIdToken: UserIdToken): Pair<Int, Boolean?>

    suspend fun tryLogIn(authorizationRequestEntity: AuthorizationRequestEntity): Pair<Int, LogInResponseEntity?>

    suspend fun trySignUp(authorizationRequestEntity: AuthorizationRequestEntity): Int

    suspend fun trySignOut(signOutRequestEntity: SignOutRequestEntity): Int

    suspend fun trySignInWithGoogle(signInWithGoogleRequest: SignInWithGoogleRequest): Pair<Int, LogInResponseEntity?>
}