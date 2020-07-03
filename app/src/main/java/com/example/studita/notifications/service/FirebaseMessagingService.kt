package com.example.studita.notifications.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.studita.R
import com.example.studita.data.entity.toNotificationType
import com.example.studita.domain.entity.*
import com.example.studita.presentation.activities.MainMenuActivity
import com.example.studita.presentation.draw.AvaDrawer
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.UserUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random


class FirebaseMessagingService : FirebaseMessagingService(){

    companion object{
        const val CHANNEL_ID = "studitaNotificationsId"
    }

    lateinit var notificationData: NotificationData

    var target = object : CustomTarget<Bitmap>(){

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


    override fun onMessageReceived(p0: RemoteMessage) {

        if(PrefsUtils.notificationsAreEnabled()) {
            val notificationType = p0.data["type"]!!.first().toNotificationType()

            val isMyFriendData = when (notificationType) {
                NotificationType.FRIENDSHIP_REQUEST -> IsMyFriendData(
                    friendshipRequestFromMe = false,
                    friendshipRequestToMe = true,
                    isMyFriend = false
                )
                NotificationType.DUEL_REQUEST -> IsMyFriendData(
                    friendshipRequestFromMe = false,
                    friendshipRequestToMe = false,
                    isMyFriend = true
                )
                NotificationType.ACCEPTED_FRIENDSHIP -> IsMyFriendData(
                    friendshipRequestFromMe = true,
                    friendshipRequestToMe = false,
                    isMyFriend = false
                )
            }

            val userData = UserData(
                p0.data["user_id"]!!.toInt(),
                p0.data["user_name"]!!,
                p0.data["avatar_link"],
                isMyFriendData.toStatus(p0.data["user_id"]!!.toInt())
            )

            notificationData = NotificationData(
                userData.userId,
                userData.userName,
                userData.avatarLink,
                notificationType,
                0,
                isMyFriendData
            )

            if (userData.avatarLink == null) {
                showNotification(AvaDrawer.getBitmap(userData.userName, userData.userId, this))
            } else {
                Glide.with(this)
                    .asBitmap()
                    .load(userData.avatarLink)
                    .centerCrop()
                    .apply(RequestOptions.circleCropTransform())
                    .into(target)
            }
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    private fun showNotification(largeIcon: Bitmap) {
        createNotificationChannel()

        val intent =
            Intent(this, MainMenuActivity::class.java)
        intent.putExtra("OPEN_NOTIFICATIONS", true)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) R.drawable.notification_icon else R.drawable.notification_icon_below_pie)
            .setContentIntent(pendingIntent)
            .setLargeIcon(largeIcon)
            .setColor(ContextCompat.getColor(this, R.color.blue))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        when(notificationData.notificationType){
            NotificationType.FRIENDSHIP_REQUEST -> {
                notification
                    .setContentTitle(resources.getString(R.string.notification_friend_reqest_title))
                    .setContentText("${resources.getString(R.string.user_name_template, notificationData.userName)} " +
                            resources.getString(R.string.notification_type_request_friendship)
                    )
                    .addAction(0, resources.getString(R.string.accept) as CharSequence, null)
                    .addAction(0, resources.getString(R.string.reject) as CharSequence, null)
            }
            NotificationType.DUEL_REQUEST -> {
                notification
                    .setContentTitle(resources.getString(R.string.notification_duel_missed_call_title))
                    .setContentText("${resources.getString(R.string.user_name_template, notificationData.userName)} " +
                            resources.getString(R.string.notification_duel_missed_call_subtitle)
                    )
            }
            NotificationType.ACCEPTED_FRIENDSHIP -> {
                notification
                    .setContentTitle(resources.getString(R.string.notification_friendship_request_accepted_title))
                    .setContentText("${resources.getString(R.string.user_name_template, notificationData.userName)} " +
                            resources.getString(R.string.notification_type_accepted_friendship)
                    )
            }
        }
        NotificationManagerCompat.from(this).notify(Random.nextInt(3000), notification.build())
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notifications_channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}