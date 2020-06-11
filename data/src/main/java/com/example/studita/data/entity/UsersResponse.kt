package com.example.studita.data.entity

import com.example.studita.domain.entity.UserData
import com.google.gson.annotations.SerializedName

data class UsersResponse(@SerializedName("users_count")val usersCount: Int, @SerializedName("users")val users: List<UserEntity>)

data class UserEntity(@SerializedName("user_id")val userId: Int,
                      @SerializedName("user_name")val userName: String,
                      @SerializedName("avatar_link")val avatarLink: String?,
                      @SerializedName("is_my_friend")val isMyFriend: Boolean)

fun UserEntity.toBusinessEntity() = UserData(userId, userName, avatarLink, isMyFriend)
