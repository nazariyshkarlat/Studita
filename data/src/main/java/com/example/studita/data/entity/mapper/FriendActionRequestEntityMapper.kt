package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.FriendActionRequest
import com.example.studita.domain.entity.FriendActionRequestData

class FriendActionRequestEntityMapper(private val userIdTokenMapper: UserIdTokenMapper) : Mapper<FriendActionRequestData, FriendActionRequest>{
    override fun map(source: FriendActionRequestData) = FriendActionRequest(userIdTokenMapper.map(source.userIdToken), source.friendId)

}