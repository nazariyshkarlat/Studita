package com.studita.data.entity

import com.studita.domain.entity.UserData
import com.studita.domain.entity.toStatus
import com.google.gson.annotations.SerializedName

data class UsersResponse(
    @SerializedName("users_count") val usersCount: Int,
    @SerializedName("users") val users: List<UserEntity>
)

data class UserEntity(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("user_name") val userName: String,
    @SerializedName("avatar_link") val avatarLink: String?,
    @SerializedName("friendship") val isMyFriendEntity: IsMyFriendEntity
)

fun UserEntity.toBusinessEntity() =
    UserData(userId, userName, avatarLink, isMyFriendEntity.toBusinessEntity().toStatus(userId))
