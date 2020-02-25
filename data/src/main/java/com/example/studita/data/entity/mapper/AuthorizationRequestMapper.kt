package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.AuthorizationRequestEntity
import com.example.studita.domain.entity.authorization.AuthorizationRequestData

class AuthorizationRequestMapper : Mapper<AuthorizationRequestData, AuthorizationRequestEntity>{
    override fun map(source: AuthorizationRequestData): AuthorizationRequestEntity {
        return AuthorizationRequestEntity(
            source.userEmail,
            source.userPassword
        )
    }
}