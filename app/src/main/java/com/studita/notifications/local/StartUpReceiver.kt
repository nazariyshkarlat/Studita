package com.studita.notifications.local

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.LocaleList
import android.util.Log
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.io.IOException
import java.util.*

class StartUpReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        if ("android.intent.action.BOOT_COMPLETED" == intent.action) {
            scheduleLocalNotifications(context)
        }
    }

    companion object {
        fun scheduleLocalNotifications(context: Context) {

            val now = Calendar.getInstance()
            val morningCalendar = Calendar.getInstance().apply {
                this[Calendar.HOUR_OF_DAY] = 8
                this[Calendar.MINUTE] = 0
                this[Calendar.SECOND] = 0
                if (now.after(this)) {
                    this.add(Calendar.DATE, 1)
                }
            }
            val eveningCalendar = Calendar.getInstance().apply {
                this[Calendar.HOUR_OF_DAY] = 20
                this[Calendar.MINUTE] = 0
                this[Calendar.SECOND] = 0
                if (now.after(this)) {
                    this.add(Calendar.DATE, 1)
                }
            }
            val morningPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                Intent(context, LocalNotificationReceiver::class.java).apply {
                    putExtra("IS_MORNING_NOTIFICATION", true)
                },
                PendingIntent.FLAG_CANCEL_CURRENT
            )
            val eveningPendingIntent = PendingIntent.getBroadcast(
                context,
                1,
                Intent(context, LocalNotificationReceiver::class.java).apply {
                    putExtra("IS_MORNING_NOTIFICATION", false)
                },
                PendingIntent.FLAG_CANCEL_CURRENT
            )
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, morningCalendar.timeInMillis, morningPendingIntent
            )
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, eveningCalendar.timeInMillis, eveningPendingIntent
            )
        }
    }
}