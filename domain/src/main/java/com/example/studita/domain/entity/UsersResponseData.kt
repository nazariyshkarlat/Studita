package com.example.studita.domain.entity

import kotlin.collections.ArrayList

data class UsersResponseData(var usersCount: Int, val users: ArrayList<UserData>)

data class UserData(val userId: Int, val userName: String, val avatarLink: String?, var isMyFriend: Boolean)