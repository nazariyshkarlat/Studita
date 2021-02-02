package com.studita.domain.entity

import android.app.Notification
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class NotificationData{
    abstract val userId: Int
    abstract val notificationType: NotificationType
    abstract val secondsAgo: Long

    @Serializable
    @SerialName("from_user")
    class NotificationFromUser(
        override val userId: Int,
        override val notificationType: NotificationType,
        override val secondsAgo: Long,
        val userName: String,
        val isMyFriendData: IsMyFriendData,
        val imageLink: String?
    ) : NotificationData()

    @Serializable
    @SerialName("achievement")
    class AchievementNotification(
        override val userId: Int,
        override val notificationType: NotificationType.Achievement,
        override val secondsAgo: Long,
        val title: String,
        val subtitle: String,
        val iconLink: String,
        val xpReward: Int
    ): NotificationData()
}

@Serializable
sealed class NotificationType {
    @Serializable
    @SerialName("duel_request")
    object DuelRequest : NotificationType()
    @Serializable
    @SerialName("friendship_request")
    object FriendshipRequest: NotificationType()
    @Serializable
    @SerialName("accepted_friendship")
    object AcceptedFriendship: NotificationType()
    @Serializable
    @SerialName("achievement")
    class Achievement(val type: AchievementType, val level: AchievementLevel): NotificationType()
}