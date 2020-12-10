package com.studita.notifications.local

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.studita.di.data.PrivacySettingsModule
import com.studita.di.data.SubscribeEmailModule
import com.studita.notifications.local.StartUpReceiver.Companion.scheduleLocalNotifications
import com.studita.utils.PrefsUtils
import com.studita.utils.UserUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


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