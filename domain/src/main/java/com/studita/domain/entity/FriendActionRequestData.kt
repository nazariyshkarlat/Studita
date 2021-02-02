package com.studita.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class FriendActionRequestData(val userIdToken: UserIdTokenData, val friendId: Int)