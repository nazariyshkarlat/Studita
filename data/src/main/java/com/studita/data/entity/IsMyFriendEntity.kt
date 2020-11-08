package com.studita.data.entity

import com.studita.domain.entity.IsMyFriendData
import com.google.gson.annotations.SerializedName

data class IsMyFriendEntity(
    @SerializedName("is_my_friend") val isMyFriend: Boolean = false,
    @SerializedName("friendship_from_me") val friendshipRequestFromMe: Boolean = false,
    @SerializedName("friendship_to_me") val friendshipRequestToMe: Boolean = false
)

fun IsMyFriendEntity.toBusinessEntity() =
    IsMyFriendData(isMyFriend, friendshipRequestFromMe, friendshipRequestToMe)