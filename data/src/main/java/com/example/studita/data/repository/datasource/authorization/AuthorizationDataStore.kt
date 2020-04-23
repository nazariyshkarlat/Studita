package com.example.studita.data.repository.datasource.authorization

import com.example.studita.data.entity.AuthorizationRequestEntity
import com.example.studita.data.entity.LogInResponseEntity
import com.example.studita.data.entity.SignInWithGoogleRequestEntity
import com.example.studita.data.entity.UserDataEntity

interface AuthorizationDataStore{

    suspend fun tryLogIn(authorizationRequestEntity: AuthorizationRequestEntity) : Pair<Int, LogInResponseEntity?>

    suspend fun trySignUp(authorizationRequestEntity: AuthorizationRequestEntity): Int

    suspend fun trySignInWithGoogle(signInWithGoogleRequestEntity: SignInWithGoogleRequestEntity): Pair<Int, LogInResponseEntity?>
}