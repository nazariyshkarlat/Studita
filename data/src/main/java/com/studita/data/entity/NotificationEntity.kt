package com.studita.data.entity

import com.google.gson.annotations.SerializedName
import com.studita.domain.entity.*

data class NotificationEntity(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("user_name") val userName: String?,
    @SerializedName("subtitle") val subtitle: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("image_url") val imageLink: String?,
    @SerializedName("notification_type") val notificationType: String,
    @SerializedName("seconds_ago") val secondsAgo: Long,
    @SerializedName("friendship") val isMyFriendEntity: IsMyFriendEntity?
)

fun NotificationEntity.toBusinessEntity() =
    if(notificationType.toNotificationType() is NotificationType.Achievement){
        NotificationData.AchievementNotification(
            userId,
            notificationType.toNotificationType() as NotificationType.Achievement,
            secondsAgo,
            title!!,
            subtitle!!,
            imageLink!!
        )
    }else{
        NotificationData.NotificationFromUser(
            userId,
            notificationType.toNotificationType(),
            secondsAgo,
            userName!!,
            isMyFriendEntity!!.toBusinessEntity(),
            imageLink
        )
    }

fun String.toNotificationType() = when (this) {
    "d" -> NotificationType.DuelRequest
    "f" -> NotificationType.FriendshipRequest
    "a" -> NotificationType.AcceptedFriendship
    else -> {
        //improvable: ach_t1(type 1)_l3(level 3)
        this.split('_').let {part->
            when (part.size) {
                3 -> {
                    NotificationType.Achievement(
                        AchievementType.values().first { it.typeNumber == part[1].replace("t", "").toInt()},
                        AchievementLevel.values().first { it.levelNumber == part[2].replace("l", "").toInt()},
                    )
                }
                2 -> {
                    NotificationType.Achievement(
                        AchievementType.values().first { it.typeNumber == part[1].replace("t", "").toInt()},
                        AchievementLevel.NO_LEVEL
                    )
                }
                else -> throw UnsupportedOperationException("unknown notification type")
            }
        }
    }
}

fun String.toFirebaseMessageType() = when (this) {
    "c" -> MessageType.FRIENDSHIP_REQUEST_CANCELLED
    "r"-> MessageType.FRIENDSHIP_REMOVED
    else -> throw UnsupportedOperationException("unknown notification type")
}


fun String.isNotificationType() = when {
    this == "d" -> true
    this == "f" -> true
    this == "a" -> true
    this.filter { it.isLetter() || it == '_' } == "ach_t_l" -> true
    else -> false
}