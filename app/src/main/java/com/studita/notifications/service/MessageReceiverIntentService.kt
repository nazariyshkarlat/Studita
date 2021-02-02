package com.studita.notifications.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.JobIntentService
import com.studita.data.entity.toFirebaseMessageType
import com.studita.domain.entity.MessageType
import com.studita.domain.entity.UserData
import com.studita.domain.interactor.IsMyFriendStatus
import com.studita.domain.interactor.users.UsersInteractor
import com.studita.notifications.PushReceiver
import com.studita.utils.UserUtils

class MessageReceiverIntentService : JobIntentService() {

    companion object {
        const val BROADCAST_MESSAGE = "BROADCAST_MESSAGE"
        private const val JOB_ID = 43245

        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, MessageReceiverIntentService::class.java, JOB_ID, work)
        }
    }

    override fun onHandleWork(intent: Intent) {

        val userId = intent.getStringExtra("user_id")!!.toInt()
        val userData = UserData(
            userId,
            intent.getStringExtra("user_name")!!,
            intent.getStringExtra("image_url"),
            IsMyFriendStatus.Success.IsNotMyFriend(userId)
        )

        when (intent.getStringExtra("type")!!.toFirebaseMessageType()) {
            MessageType.FRIENDSHIP_REQUEST_CANCELLED, MessageType.FRIENDSHIP_REMOVED -> {
                UserUtils.isMyFriendLiveData.postValue(
                    UsersInteractor.FriendActionState.FriendshipRequestIsCanceled(
                        UserData(
                            userData.userId,
                            userData.userName,
                            userData.avatarLink,
                            IsMyFriendStatus.Success.IsNotMyFriend(userData.userId)
                        )
                    )
                )
                sendBroadcastToDeleteNotification(intent.apply {
                    putExtra("user_id", userId)
                }.extras!!)
            }
        }
    }

    private fun sendBroadcastToDeleteNotification(extras: Bundle) {
        val broadcast = Intent()
        broadcast.putExtras(extras)
        broadcast.action = BROADCAST_MESSAGE
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