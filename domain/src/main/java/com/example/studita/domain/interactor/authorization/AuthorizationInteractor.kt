package com.example.studita.domain.interactor.authorization

import com.example.studita.domain.entity.authorization.AuthorizationRequestData
import com.example.studita.domain.interactor.LogInStatus
import com.example.studita.domain.interactor.SignUpStatus

interface AuthorizationInteractor{

    suspend fun signUp(authorizationRequestData: AuthorizationRequestData): SignUpStatus
    suspend fun logIn(authorizationRequestData: AuthorizationRequestData): LogInStatus

}