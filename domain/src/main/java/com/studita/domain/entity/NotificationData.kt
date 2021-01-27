package com.studita.domain.entity

import android.app.Notification
import kotlinx.serialization.Serializable

@Serializable
sealed class NotificationData{
    abstract val userId: Int
    abstract val notificationType: NotificationType
    abstract val secondsAgo: Long

    @Serializable
    class NotificationFromUser(
        override val userId: Int,
        override val notificationType: NotificationType,
        override val secondsAgo: Long,
        val userName: String,
        val isMyFriendData: IsMyFriendData,
        val imageLink: String?
    ) : NotificationData()

    @Serializable
    class AchievementNotification(
        override val userId: Int,
        override val notificationType: NotificationType.Achievement,
        override val secondsAgo: Long,
        val title: String,
        val subtitle: String,
        val iconLink: String,
    ): NotificationData()
}

@Serializable
sealed class NotificationType {
    @Serializable
    object DuelRequest : NotificationType()
    @Serializable
    object FriendshipRequest: NotificationType()
    @Serializable
    object AcceptedFriendship: NotificationType()
    @Serializable
    class Achievement(val type: AchievementType, val level: AchievementLevel): NotificationType()
}