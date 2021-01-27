package com.studita.notifications.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.JobIntentService
import androidx.core.os.bundleOf
import com.studita.data.entity.toNotificationType
import com.studita.domain.entity.IsMyFriendData
import com.studita.domain.entity.NotificationData
import com.studita.domain.entity.NotificationType
import com.studita.domain.entity.serializer.IsMyFriendStatusSerializer
import com.studita.domain.interactor.IsMyFriendStatus
import com.studita.notifications.PushReceiver
import com.google.gson.GsonBuilder
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.UnsupportedOperationException


class PushReceiverIntentService : JobIntentService() {

    companion object {
        const val BROADCAST_NOTIFICATION = "BROADCAST_NOTIFICATION"
        private const val JOB_ID = 34556

        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, PushReceiverIntentService::class.java, JOB_ID, work)
        }

    }

    override fun onHandleWork(intent: Intent) {

        val notificationType =
            (intent.getStringExtra("type") as String).toNotificationType()

        when (notificationType) {
            !is NotificationType.Achievement -> {
                val isMyFriendData = when (notificationType) {
                    NotificationType.FriendshipRequest -> IsMyFriendData(
                        friendshipRequestFromMe = false,
                        friendshipRequestToMe = true,
                        isMyFriend = false
                    )
                    NotificationType.DuelRequest -> IsMyFriendData(
                        friendshipRequestFromMe = false,
                        friendshipRequestToMe = false,
                        isMyFriend = true
                    )
                    NotificationType.AcceptedFriendship -> IsMyFriendData(
                        friendshipRequestFromMe = false,
                        friendshipRequestToMe = false,
                        isMyFriend = true
                    )
                    else -> throw UnsupportedOperationException("incorrect notification type")
                }
                val notificationData: NotificationData.NotificationFromUser = NotificationData.NotificationFromUser(
                    (intent.getStringExtra("user_id") as String).toInt(),
                    notificationType,
                    0,
                    (intent.getStringExtra("user_name") as String),
                    isMyFriendData,
                    intent.getStringExtra("avatar_link"),
                )
                sendNotification(bundleOf(
                    "NOTIFICATION_DATA" to Json.encodeToString(notificationData),
                    "type" to intent.getStringExtra("type")!!))
            }
            else -> {
                val notificationData: NotificationData.AchievementNotification = NotificationData.AchievementNotification(
                    (intent.getStringExtra("user_id") as String).toInt(),
                    notificationType,
                    0,
                    (intent.getStringExtra("title") as String),
                    (intent.getStringExtra("subtitle") as String),
                    (intent.getStringExtra("image_url") as String),
                )
                sendNotification(bundleOf(
                    "NOTIFICATION_DATA" to Json.encodeToString(notificationData),
                    "type" to intent.getStringExtra("type")!!))
            }
        }
    }

    private fun sendNotification(extras: Bundle) {
        val broadcast = Intent()
        broadcast.putExtras(extras)
        broadcast.action = BROADCAST_NOTIFICATION
        sendOrderedBroadcast(
            broadcast,
            null,
            PushReceiver(),
            null,
            Activity.RESULT_OK,
            null,
            extras
        )
    }

}