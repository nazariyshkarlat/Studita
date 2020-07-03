package com.example.studita.domain.entity

import com.example.studita.domain.interactor.IsMyFriendStatus

data class NotificationData(val userId: Int,
                            val userName: String,
                            val avatarLink: String?,
                            val notificationType: NotificationType,
                            val secondsAgo: Long,
                            val isMyFriendData: IsMyFriendData)

enum class NotificationType{
    DUEL_REQUEST,
    FRIENDSHIP_REQUEST,
    ACCEPTED_FRIENDSHIP
}
