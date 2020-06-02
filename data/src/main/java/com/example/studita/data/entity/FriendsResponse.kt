package com.example.studita.data.entity

import com.google.gson.annotations.SerializedName
import java.util.*

data class FriendsResponse(@SerializedName("friends_count")val friendsCount: Int, @SerializedName("friends")val friends: List<FriendEntity>)

data class FriendEntity(@SerializedName("friend_id")val friendId: Int, @SerializedName("friend_name")val friendName: String, @SerializedName("avatar_link")val avatarLink: String?, @SerializedName("datetime_added")val dateTime: String)