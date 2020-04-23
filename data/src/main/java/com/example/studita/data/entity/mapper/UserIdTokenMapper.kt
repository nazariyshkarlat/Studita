package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.UserIdToken
import com.example.studita.domain.entity.UserIdTokenData

class UserIdTokenMapper : Mapper<UserIdTokenData, UserIdToken>{

    override fun map(source: UserIdTokenData): UserIdToken = UserIdToken(source.userId, source.userToken)

}