package com.studita.data.entity

import com.studita.domain.entity.UserData
import com.studita.domain.entity.toStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsersResponse(
    @SerialName("users_count") val usersCount: Int,
    @SerialName("users") val users: List<UserEntity>
)

@Serializable
data class UserEntity(
    @SerialName("user_id") val userId: Int,
    @SerialName("user_name") val userName: String,
    @SerialName("avatar_link") val avatarLink: String?,
    @SerialName("friendship") val isMyFriendEntity: IsMyFriendEntity
)

fun UserEntity.toBusinessEntity() =
    UserData(userId, userName, avatarLink, isMyFriendEntity.toBusinessEntity().toStatus(userId))
