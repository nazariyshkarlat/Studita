package com.studita.domain.entity

import com.studita.domain.interactor.IsMyFriendStatus
import kotlinx.serialization.Serializable

@Serializable
data class IsMyFriendData(
    val isMyFriend: Boolean = false,
    val friendshipRequestFromMe: Boolean = false,
    val friendshipRequestToMe: Boolean = false
)

fun IsMyFriendData.toStatus(userId: Int) = when {
    isMyFriend -> IsMyFriendStatus.Success.IsMyFriend(userId)
    friendshipRequestFromMe -> IsMyFriendStatus.Success.GotMyFriendshipRequest(userId)
    friendshipRequestToMe -> IsMyFriendStatus.Success.WaitingForFriendshipAccept(userId)
    else -> IsMyFriendStatus.Success.IsNotMyFriend(userId)
}

