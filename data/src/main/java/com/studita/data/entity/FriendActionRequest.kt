package com.studita.data.entity

import com.studita.domain.entity.FriendActionRequestData
import com.google.gson.annotations.SerializedName

data class FriendActionRequest(
    @SerializedName("auth_data") val userIdToken: UserIdToken,
    @SerializedName("friend_id") val friendId: Int
)

fun FriendActionRequest.toBusinessEntity() =
    FriendActionRequestData(userIdToken.toBusinessEntity(), friendId)

fun FriendActionRequestData.toRawEntity() = FriendActionRequest(userIdToken.toRawEntity(), friendId)