package com.example.studita.notifications

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context

import android.content.Intent
import android.util.Log
import com.example.studita.notifications.service.PushIntentService


class PushReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        if(resultCode == Activity.RESULT_OK) {
            val push = Intent(context, PushIntentService::class.java)
            push.putExtras(getResultExtras(true))
            PushIntentService.enqueueWork(context, push)
        }
    }
}