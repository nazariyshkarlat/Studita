package com.studita.notifications

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.studita.notifications.service.PushIntentService
import com.studita.notifications.service.PushReceiverIntentService.Companion.BROADCAST_NOTIFICATION


class PushReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        if (resultCode == Activity.RESULT_OK && intent.action == BROADCAST_NOTIFICATION) {
            val push = Intent(context, PushIntentService::class.java)
            push.putExtras(getResultExtras(true))
            PushIntentService.enqueueWork(context, push)
        }
    }
}