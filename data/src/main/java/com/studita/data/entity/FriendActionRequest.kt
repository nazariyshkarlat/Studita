package com.studita.data.entity

import com.studita.domain.entity.FriendActionRequestData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendActionRequest(
    @SerialName("auth_data") val userIdToken: UserIdToken,
    @SerialName("friend_id") val friendId: Int
)

fun FriendActionRequest.toBusinessEntity() =
    FriendActionRequestData(userIdToken.toBusinessEntity(), friendId)

fun FriendActionRequestData.toRawEntity() = FriendActionRequest(userIdToken.toRawEntity(), friendId)