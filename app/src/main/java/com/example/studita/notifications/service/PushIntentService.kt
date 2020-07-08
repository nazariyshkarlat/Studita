package com.example.studita.notifications.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.studita.R
import com.example.studita.domain.entity.NotificationData
import com.example.studita.domain.entity.NotificationType
import com.example.studita.domain.entity.UserData
import com.example.studita.domain.entity.serializer.IsMyFriendStatusDeserializer
import com.example.studita.domain.entity.serializer.IsMyFriendStatusSerializer
import com.example.studita.domain.entity.toStatus
import com.example.studita.domain.interactor.IsMyFriendStatus
import com.example.studita.notifications.NotificationsActionsHandleBroadcastReceiver
import com.example.studita.notifications.toActionString
import com.example.studita.presentation.activities.MainMenuActivity
import com.example.studita.presentation.draw.AvaDrawer
import com.example.studita.utils.IDUtils.createID
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.UserUtils
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken


class PushIntentService : JobIntentService(){

    companion object{
        const val CHANNEL_ID = "studitaNotificationsId"
        private const val JOB_ID = 6745

        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, PushIntentService::class.java, JOB_ID, work)
        }
    }

    lateinit var intent: Intent

    lateinit var notificationData: NotificationData

    private var target = object : CustomTarget<Bitmap>(){

        override fun onLoadCleared(placeholder: Drawable?) {
            // this is called when imageView is cleared on lifecycle call or for
            // some other reason.
            // if you are referencing the bitmap somewhere else too other than this imageView
            // clear it here as you can no longer have the bitmap
        }

        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            showNotification(resource)
        }
    }

    override fun onHandleWork(intent: Intent) {

        this.intent = intent

        notificationData = GsonBuilder().apply {
            registerTypeAdapter(IsMyFriendStatus::class.java, IsMyFriendStatusDeserializer())
        }.create().fromJson<NotificationData>(
            intent.getStringExtra("NOTIFICATION_DATA"),
            object : TypeToken<NotificationData>() {}.type
        )

        if (notificationData.avatarLink == null) {
            showNotification(
                AvaDrawer.getBitmap(
                    notificationData.userName,
                    notificationData.userId,
                    this
                )
            )
        } else {
            Glide.with(this)
                .asBitmap()
                .load(notificationData.avatarLink)
                .centerCrop()
                .apply(RequestOptions.circleCropTransform())
                .into(target)
        }
    }


    private fun showNotification(largeIcon: Bitmap) {
        createNotificationChannel()

        val notificationId = createID()

        val actionIntent =
            Intent(this, NotificationsActionsHandleBroadcastReceiver::class.java)

        val userData = UserData(notificationData.userId, notificationData.userName, notificationData.avatarLink, notificationData.isMyFriendData.toStatus(notificationData.userId))

        actionIntent.putExtra("NOTIFICATION_ID", notificationId)
        actionIntent.putExtra("USER_DATA", GsonBuilder().apply {
            registerTypeAdapter(IsMyFriendStatus::class.java, IsMyFriendStatusSerializer())
        }.create().toJson(userData))

        val openNotificationsPendingIntent = PendingIntent.getActivity(
            this, MainMenuActivity.NOTIFICATIONS_REQUEST_CODE,
            Intent(applicationContext, MainMenuActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("REQUEST_CODE", MainMenuActivity.NOTIFICATIONS_REQUEST_CODE)
            }, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val acceptFriendshipPendingIntent = PendingIntent.getBroadcast(this, 0, actionIntent.apply {
            action = NotificationsActionsHandleBroadcastReceiver.NotificationAction.ACCEPT_FRIENDSHIP.toActionString()
        }, PendingIntent.FLAG_UPDATE_CURRENT)

        val rejectFriendshipPendingIntent = PendingIntent.getBroadcast(this, 0, actionIntent.apply {
            action = NotificationsActionsHandleBroadcastReceiver.NotificationAction.REJECT_FRIENDSHIP.toActionString()
        }, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this,
            CHANNEL_ID
        )
            .setSmallIcon(if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) R.drawable.notification_icon_below_pie else R.drawable.notification_icon)
            .setContentIntent(openNotificationsPendingIntent)
            .setLargeIcon(largeIcon)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(this, R.color.blue))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        when(notificationData.notificationType){
            NotificationType.FRIENDSHIP_REQUEST -> {
                notification
                    .setContentTitle(this.resources.getString(R.string.notification_friend_reqest_title))
                    .setContentText("${this.resources.getString(R.string.user_name_template, notificationData.userName)} " +
                            this.resources.getString(R.string.notification_type_request_friendship)
                    )
                    .addAction(0, this.resources.getString(R.string.accept) as CharSequence, acceptFriendshipPendingIntent)
                    .addAction(0, this.resources.getString(R.string.reject) as CharSequence, rejectFriendshipPendingIntent)
                UserUtils.isMyFriendLiveData.postValue(UserData(notificationData.userId, notificationData.userName, notificationData.avatarLink, IsMyFriendStatus.Success.WaitingForFriendshipAccept(notificationData.userId)))
            }
            NotificationType.DUEL_REQUEST -> {
                notification
                    .setContentTitle(this.resources.getString(R.string.notification_duel_missed_call_title))
                    .setContentText("${this.resources.getString(R.string.user_name_template, notificationData.userName)} " +
                            this.resources.getString(R.string.notification_duel_missed_call_subtitle)
                    )
            }
            NotificationType.ACCEPTED_FRIENDSHIP -> {
                notification
                    .setContentTitle(this.resources.getString(R.string.notification_friendship_request_accepted_title))
                    .setContentText("${this.resources.getString(R.string.user_name_template, notificationData.userName)} " +
                            this.resources.getString(R.string.notification_type_accepted_friendship)
                    )
                UserUtils.isMyFriendLiveData.postValue(UserData(notificationData.userId, notificationData.userName, notificationData.avatarLink, IsMyFriendStatus.Success.IsMyFriend(notificationData.userId)))
            }
        }

        if(UserUtils.userDataLiveData.value != null) {
            UserUtils.userDataLiveData.postValue(UserUtils.userData.apply {
                notificationsAreChecked = false
            })
        }

        if(PrefsUtils.notificationsAreEnabled())
            NotificationManagerCompat.from(this).notify(notificationId, notification.build())
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = this.resources.getString(R.string.notifications_channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            val notificationManager: NotificationManager =
                this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}