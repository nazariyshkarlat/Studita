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
            (intent.getStringExtra("type") as String).first().toNotificationType()

        val isMyFriendData = when (notificationType) {
            NotificationType.FRIENDSHIP_REQUEST -> IsMyFriendData(
                friendshipRequestFromMe = false,
                friendshipRequestToMe = true,
                isMyFriend = false
            )
            NotificationType.DUEL_REQUEST -> IsMyFriendData(
                friendshipRequestFromMe = false,
                friendshipRequestToMe = false,
                isMyFriend = true
            )
            NotificationType.ACCEPTED_FRIENDSHIP -> IsMyFriendData(
                friendshipRequestFromMe = false,
                friendshipRequestToMe = false,
                isMyFriend = true
            )
        }

        val notificationData = NotificationData(

            (intent.getStringExtra("user_id") as String).toInt(),
            (intent.getStringExtra("user_name") as String),
            intent.getStringExtra("avatar_link"),
            notificationType,
            0,
            isMyFriendData
        )

        sendNotification(bundleOf("NOTIFICATION_DATA" to GsonBuilder().apply {
            registerTypeAdapter(IsMyFriendStatus.Success::class.java, IsMyFriendStatusSerializer())
        }.create().toJson(notificationData), "type" to intent.getStringExtra("type")!!))

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