package com.example.studita.data.entity

import com.example.studita.domain.entity.NotificationData
import com.example.studita.domain.entity.NotificationType
import com.google.gson.annotations.SerializedName

data class NotificationEntity(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("user_name") val userName: String,
    @SerializedName("avatar_link") val avatarLink: String?,
    @SerializedName("notification_type") val notificationType: Char,
    @SerializedName("seconds_ago") val secondsAgo: Long,
    @SerializedName("friendship") val isMyFriendEntity: IsMyFriendEntity
)

fun NotificationEntity.toBusinessEntity() = NotificationData(
    userId,
    userName,
    avatarLink,
    notificationType.toNotificationType(),
    secondsAgo,
    isMyFriendEntity.toBusinessEntity()
)

fun Char.toNotificationType() = when (this) {
    'd' -> NotificationType.DUEL_REQUEST
    'f' -> NotificationType.FRIENDSHIP_REQUEST
    'a' -> NotificationType.ACCEPTED_FRIENDSHIP
    else -> throw UnsupportedOperationException("unknown notification type")
}