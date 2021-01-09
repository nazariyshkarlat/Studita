package com.studita.notifications.local

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.studita.notifications.local.StartUpReceiver.Companion.scheduleLocalNotifications


class LocalNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        JobIntentService.enqueueWork(
            context,
            LocalNotificationsService::class.java,
            LocalNotificationsService.JOB_ID,
            intent
        )
        scheduleLocalNotifications(context)
    }
}