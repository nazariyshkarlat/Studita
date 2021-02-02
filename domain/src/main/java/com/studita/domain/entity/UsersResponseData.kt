package com.studita.domain.entity

import com.studita.domain.interactor.IsMyFriendStatus
import kotlinx.serialization.Serializable

@Serializable
data class UsersResponseData(var usersCount: Int, val users: ArrayList<UserData>)

@Serializable
data class UserData(
    val userId: Int,
    val userName: String,
    val avatarLink: String?,
    var isMyFriendStatus: IsMyFriendStatus.Success
)