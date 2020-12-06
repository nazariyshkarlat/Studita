package com.studita.notifications.service

import android.content.Intent
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.studita.data.entity.isNotificationType


class FirebaseMessagingService : FirebaseMessagingService() {


    override fun onMessageReceived(p0: RemoteMessage) {
        val intent = Intent(this, PushReceiverIntentService::class.java)
        intent.putExtras(bundleOf(*p0.data.map { it.key to it.value }.toTypedArray()))

        if(intent.getStringExtra("type")!!.first().isNotificationType())
            PushReceiverIntentService.enqueueWork(this, intent)
        else
            MessageReceiverIntentService.enqueueWork(this, intent)

    }


    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

}