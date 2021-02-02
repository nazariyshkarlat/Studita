package com.studita.notifications.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationManagerCompat
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.target.Target
import coil.transform.CircleCropTransformation
import com.studita.R
import com.studita.data.entity.toNotificationType
import com.studita.domain.entity.NotificationData
import com.studita.domain.entity.NotificationType
import com.studita.domain.entity.UserData
import com.studita.domain.entity.toStatus
import com.studita.domain.interactor.IsMyFriendStatus
import com.studita.domain.interactor.users.UsersInteractor
import com.studita.notifications.NotificationsActionsHandleBroadcastReceiver
import com.studita.notifications.toActionString
import com.studita.presentation.activities.MainMenuActivity
import com.studita.presentation.draw.AvaDrawer
import com.studita.utils.IDUtils.createID
import com.studita.utils.NotificationsUtils.buildDefaultNotification
import com.studita.utils.NotificationsUtils.createNotificationChannel
import com.studita.utils.NotificationsUtils.setText
import com.studita.utils.PrefsUtils
import com.studita.utils.UserUtils
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class PushIntentService : JobIntentService() {

    companion object {
        const val CHANNEL_ID = "studitaNotificationsId"
        private const val JOB_ID = 6745

        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, PushIntentService::class.java, JOB_ID, work)
        }
    }

    lateinit var intent: Intent

    lateinit var notificationData: NotificationData

    private var target: Target =  object : Target{
        override fun onSuccess(result: Drawable) {
            super.onSuccess(result)

            if(notificationData.notificationType !is NotificationType.Achievement)
                showNotificationFromUser((result as BitmapDrawable).bitmap)
            else
                showAchievementNotification((result as BitmapDrawable).bitmap)
        }
    }

    override fun onHandleWork(intent: Intent) {

        this.intent = intent
        val notifType = intent.getStringExtra("type")!!.toNotificationType()

        if(notifType !is NotificationType.Achievement) {
            notificationData = Json.decodeFromString<NotificationData.NotificationFromUser>(intent.getStringExtra("NOTIFICATION_DATA")!!)

            (notificationData as NotificationData.NotificationFromUser).let {

                if (it.imageLink == null) {
                    showNotificationFromUser(
                        AvaDrawer.getBitmap(
                            it.userName,
                            notificationData.userId,
                            this
                        )
                    )
                } else {
                    val request = ImageRequest.Builder(this)
                        .data(it.imageLink)
                        .target(target)
                        .transformations(CircleCropTransformation())
                        .build()
                    ImageLoader(this).enqueue(request)
                }
            }
        }else {
            notificationData = Json.decodeFromString<NotificationData.AchievementNotification>(intent.getStringExtra("NOTIFICATION_DATA")!!)

            (notificationData as NotificationData.AchievementNotification).let {
                val imageLoader = ImageLoader.Builder(this)
                    .componentRegistry {
                        add(SvgDecoder(this@PushIntentService))
                    }
                    .build()
                val request = ImageRequest.Builder(this)
                    .data(it.iconLink)
                    .target(target)
                    .transformations(CircleCropTransformation())
                    .build()
                imageLoader.enqueue(request)
            }
        }
    }


    private fun showAchievementNotification(largeIcon: Bitmap){
        (notificationData as NotificationData.AchievementNotification).let {

            createNotificationChannel()

            val notificationId = createID()

            val actionIntent =
                Intent(this, NotificationsActionsHandleBroadcastReceiver::class.java)

            actionIntent.putExtra("NOTIFICATION_ID", notificationId)

            val openNotificationsPendingIntent = PendingIntent.getActivity(
                this, MainMenuActivity.NOTIFICATIONS_REQUEST_CODE,
                Intent(applicationContext, MainMenuActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    putExtra("REQUEST_CODE", MainMenuActivity.NOTIFICATIONS_REQUEST_CODE)
                }, PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notification = buildDefaultNotification()
                .setContentIntent(openNotificationsPendingIntent)
                .setLargeIcon(largeIcon)
                .setText(
                    it.title,
                    it.subtitle
                )

            if (UserUtils.userDataNotNull()) {
                UserUtils.userDataLiveData.postValue(UserUtils.userData.apply {
                    notificationsAreChecked = false
                })
            }

            if (PrefsUtils.notificationsAreEnabled())
                NotificationManagerCompat.from(this).notify(notificationId, notification.build())
        }
    }


    private fun showNotificationFromUser(largeIcon: Bitmap) {
        (notificationData as NotificationData.NotificationFromUser).let {

            createNotificationChannel()

            val notificationId = createID()

            val actionIntent =
                Intent(this, NotificationsActionsHandleBroadcastReceiver::class.java)

            val userData = UserData(
                notificationData.userId,
                it.userName,
                it.imageLink,
                it.isMyFriendData.toStatus(notificationData.userId)
            )

            actionIntent.putExtra("NOTIFICATION_ID", notificationId)
            actionIntent.putExtra("USER_DATA", Json.encodeToString(userData))

            val openNotificationsPendingIntent = PendingIntent.getActivity(
                this, MainMenuActivity.NOTIFICATIONS_REQUEST_CODE,
                Intent(applicationContext, MainMenuActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    putExtra("REQUEST_CODE", MainMenuActivity.NOTIFICATIONS_REQUEST_CODE)
                }, PendingIntent.FLAG_UPDATE_CURRENT
            )

            val acceptFriendshipPendingIntent =
                PendingIntent.getBroadcast(this, 0, actionIntent.apply {
                    action =
                        NotificationsActionsHandleBroadcastReceiver.NotificationAction.ACCEPT_FRIENDSHIP.toActionString()
                }, PendingIntent.FLAG_UPDATE_CURRENT)

            val rejectFriendshipPendingIntent =
                PendingIntent.getBroadcast(this, 0, actionIntent.apply {
                    action =
                        NotificationsActionsHandleBroadcastReceiver.NotificationAction.REJECT_FRIENDSHIP.toActionString()
                }, PendingIntent.FLAG_UPDATE_CURRENT)

            val notification = buildDefaultNotification()
                .setContentIntent(openNotificationsPendingIntent)
                .setLargeIcon(largeIcon)

            when (notificationData.notificationType) {
                NotificationType.FriendshipRequest -> {
                    notification
                        .setText(
                            this.resources.getString(R.string.notification_friend_reqest_title),
                            "${
                                this.resources.getString(
                                    R.string.user_name_template,
                                    it.userName
                                )
                            } " +
                                    this.resources.getString(R.string.notification_type_request_friendship)
                        )
                        .addAction(
                            0,
                            this.resources.getString(R.string.accept) as CharSequence,
                            acceptFriendshipPendingIntent
                        )
                        .addAction(
                            0,
                            this.resources.getString(R.string.reject) as CharSequence,
                            rejectFriendshipPendingIntent
                        )
                    UserUtils.isMyFriendLiveData.postValue(
                        UsersInteractor.FriendActionState.FriendshipRequestIsSent(
                            UserData(
                                notificationData.userId,
                                it.userName,
                                it.imageLink,
                                IsMyFriendStatus.Success.WaitingForFriendshipAccept(notificationData.userId)
                            )
                        )
                    )
                }
                NotificationType.DuelRequest -> {
                    notification
                        .setText(
                            this.resources.getString(R.string.notification_duel_missed_call_title),
                            "${
                                this.resources.getString(
                                    R.string.user_name_template,
                                    it.userName
                                )
                            } " +
                                    this.resources.getString(R.string.notification_duel_missed_call_subtitle)
                        )
                }
                NotificationType.AcceptedFriendship -> {
                    notification
                        .setText(
                            this.resources.getString(R.string.notification_friendship_request_accepted_title),
                            "${
                                this.resources.getString(
                                    R.string.user_name_template,
                                    it.userName
                                )
                            } " +
                                    this.resources.getString(R.string.notification_type_accepted_friendship)
                        )
                    UserUtils.isMyFriendLiveData.postValue(
                        UsersInteractor.FriendActionState.FriendshipRequestIsAccepted(
                            UserData(
                                notificationData.userId,
                                it.userName,
                                it.imageLink,
                                IsMyFriendStatus.Success.IsMyFriend(notificationData.userId)
                            )
                        )
                    )
                }
            }

            if (UserUtils.userDataNotNull()) {
                UserUtils.userDataLiveData.postValue(UserUtils.userData.apply {
                    notificationsAreChecked = false
                })
            }

            if (PrefsUtils.notificationsAreEnabled())
                NotificationManagerCompat.from(this).notify(notificationId, notification.build())
        }
    }
}