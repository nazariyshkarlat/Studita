package com.studita.notifications.local

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationManagerCompat
import com.studita.App
import com.studita.R
import com.studita.domain.entity.UserDataData
import com.studita.domain.interactor.UserDataStatus
import com.studita.domain.interactor.user_data.UserDataInteractor
import com.studita.presentation.activities.MainActivity
import com.studita.utils.IDUtils.createID
import com.studita.utils.NotificationsUtils.buildDefaultNotification
import com.studita.utils.NotificationsUtils.createNotificationChannel
import com.studita.utils.NotificationsUtils.setText
import com.studita.utils.PrefsUtils
import com.studita.utils.UserUtils
import com.studita.utils.UserUtils.streakActivated
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.context.GlobalContext
import java.util.concurrent.atomic.AtomicBoolean


class LocalNotificationsService : JobIntentService(){

    companion object{
        const val JOB_ID = 67454
        val APP_IS_IN_FOREGROUND = AtomicBoolean(false)
    }

    override fun onHandleWork(intent: Intent) {

        runBlocking {
            GlobalScope.launch {
                val userData = App.userDataDeferred.await()
                if (userData is UserDataStatus.Success) {
                    if (PrefsUtils.notificationsAreEnabled() && !APP_IS_IN_FOREGROUND.get()) {
                        showNotification(
                            UserUtils.userData,
                            intent.getBooleanExtra("IS_MORNING_NOTIFICATION", true)
                        )
                    }
                } else {
                    val localUserData = GlobalContext.get().get<UserDataInteractor>().getUserData(
                        PrefsUtils.getUserId(),
                        true,
                        isMyUserData = true
                    )
                    if (localUserData is UserDataStatus.Success) {
                        if (PrefsUtils.notificationsAreEnabled() && !APP_IS_IN_FOREGROUND.get())
                            showNotification(
                                localUserData.result,
                                intent.getBooleanExtra("IS_MORNING_NOTIFICATION", true)
                            )
                    }
                }
            }
        }
    }

    private fun showNotification(userData: UserDataData, isMorningNotification: Boolean){

        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP
                or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val intent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, 0
        )

        val notification = when  {
            userData.streakDays == 0 -> {
                 buildDefaultNotification()
                     .setText(resources.getString(R.string.local_notification_no_streak_title),
                         resources.getString(R.string.local_notification_no_streak_content)
                     ).setContentIntent(intent)
            }
            !streakActivated(userData.streakDatetime) -> {

                if(isMorningNotification) {
                    buildDefaultNotification()
                        .setText(resources.getString(R.string.local_notification_streak_morning_title),
                            resources.getString(R.string.local_notification_streak_morning_content)
                        ).setContentIntent(intent)
                }else{
                    buildDefaultNotification()
                        .setText(resources.getString(R.string.local_notification_streak_evening_title),
                            resources.getString(R.string.local_notification_streak_evening_content)
                        ).setContentIntent(intent)
                }
            }
            else -> null

        }

        if(notification != null) {
            val id = createID()
            NotificationManagerCompat.from(this).notify(id, notification.build())
            PrefsUtils.setLocalNotificationId(id)
        }
    }

}