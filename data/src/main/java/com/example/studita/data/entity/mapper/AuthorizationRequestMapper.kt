package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.AuthorizationRequestEntity
import com.example.studita.data.entity.UserDataEntity
import com.example.studita.domain.entity.authorization.AuthorizationRequestData

class AuthorizationRequestMapper(private val userDataEntityMapper: UserDataEntityMapper) : Mapper<AuthorizationRequestData, AuthorizationRequestEntity>{
    override fun map(source: AuthorizationRequestData): AuthorizationRequestEntity {
        return AuthorizationRequestEntity(
            source.userEmail,
            source.userPassword,
            source.userDataData?.let{userDataEntityMapper.map(it)}
        )
    }
}