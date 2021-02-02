package com.studita.data.entity

import com.studita.domain.entity.IsMyFriendData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IsMyFriendEntity(
    @SerialName("is_my_friend") val isMyFriend: Boolean = false,
    @SerialName("friendship_from_me") val friendshipRequestFromMe: Boolean = false,
    @SerialName("friendship_to_me") val friendshipRequestToMe: Boolean = false
)

fun IsMyFriendEntity.toBusinessEntity() =
    IsMyFriendData(isMyFriend, friendshipRequestFromMe, friendshipRequestToMe)