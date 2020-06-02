package com.example.studita.data.entity

import com.google.gson.annotations.SerializedName

data class FriendActionRequest(@SerializedName("auth_data")val userIdToken: UserIdToken, @SerializedName("friend_id")val friendId: Int)