package com.example.studita.domain.entity

import com.example.studita.domain.interactor.IsMyFriendStatus

data class UsersResponseData(var usersCount: Int, val users: ArrayList<UserData>)

data class UserData(
    val userId: Int,
    val userName: String,
    val avatarLink: String?,
    var isMyFriendStatus: IsMyFriendStatus.Success
)