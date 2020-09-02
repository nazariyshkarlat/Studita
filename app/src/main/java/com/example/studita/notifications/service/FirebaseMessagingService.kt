package com.example.studita.notifications.service

import android.content.Intent
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FirebaseMessagingService : FirebaseMessagingService() {


    override fun onMessageReceived(p0: RemoteMessage) {
        val intent = Intent(this, PushReceiverIntentService::class.java)
        intent.putExtras(bundleOf(*p0.data.map { it.key to it.value }.toTypedArray()))
        PushReceiverIntentService.enqueueWork(this, intent)
    }


    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

}