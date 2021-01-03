package com.studita.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.studita.R
import com.studita.notifications.service.PushIntentService

object NotificationsUtils {

    fun Context.createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = this.resources.getString(R.string.notifications_channel_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(PushIntentService.CHANNEL_ID, name, importance)
            val notificationManager: NotificationManager =
                this.getSystemService(JobIntentService.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun Context.buildDefaultNotification() = NotificationCompat.Builder(
        this,
        PushIntentService.CHANNEL_ID
    )
        .setSmallIcon(if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) R.drawable.notification_icon_below_pie else R.drawable.notification_icon)
        .setAutoCancel(true)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setColor(ContextCompat.getColor(this, if(ThemeUtils.getDefaultTheme(resources)== ThemeUtils.Theme.DARK) R.color.blue_dark_theme else R.color.blue_light_theme))
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    fun NotificationCompat.Builder.setText(title: String, content: String): NotificationCompat.Builder{
        return this
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(null))
    }
}