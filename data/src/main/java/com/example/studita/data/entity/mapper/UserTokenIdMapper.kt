package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.UserTokenId
import com.example.studita.domain.entity.UserTokenIdData

class UserTokenIdMapper : Mapper<UserTokenIdData, UserTokenId>{

    override fun map(source: UserTokenIdData): UserTokenId = UserTokenId(source.userId, source.userToken)

}