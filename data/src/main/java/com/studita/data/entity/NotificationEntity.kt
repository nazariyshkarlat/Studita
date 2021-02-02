package com.studita.data.entity
import com.studita.domain.entity.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationEntity(
    @SerialName("user_id") val userId: Int,
    @SerialName("user_name") val userName: String? = null,
    @SerialName("subtitle") val subtitle: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("image_url") val imageLink: String? = null,
    @SerialName("notification_type") val notificationType: String,
    @SerialName("seconds_ago") val secondsAgo: Long,
    @SerialName("friendship") val isMyFriendEntity: IsMyFriendEntity? = null,
    @SerialName("xp_reward") val xpReward: Int? = null,
    )

fun NotificationEntity.toBusinessEntity() =
    if(notificationType.toNotificationType() is NotificationType.Achievement){
        NotificationData.AchievementNotification(
            userId,
            notificationType.toNotificationType() as NotificationType.Achievement,
            secondsAgo,
            title!!,
            subtitle!!,
            imageLink!!,
            xpReward!!
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