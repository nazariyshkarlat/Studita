package com.example.studita.domain.entity

import java.util.*
import kotlin.collections.ArrayList

data class FriendsResponseData(var friendsCount: Int, val friends: ArrayList<FriendData>)

data class FriendData(val friendId: Int, val friendName: String, val avatarLink: String?, val dateTime: Date)