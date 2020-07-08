package com.example.studita.notifications

import android.app.NotificationManager
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.studita.di.data.UsersModule
import com.example.studita.domain.entity.FriendActionRequestData
import com.example.studita.domain.entity.UserData
import com.example.studita.domain.entity.serializer.IsMyFriendStatusDeserializer
import com.example.studita.domain.interactor.IsMyFriendStatus
import com.example.studita.presentation.activities.MainMenuActivity
import com.example.studita.utils.UserUtils
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class NotificationsActionsHandleBroadcastReceiver : BroadcastReceiver(){


    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", 0)
        val userData = GsonBuilder().apply {
            registerTypeAdapter(IsMyFriendStatus::class.java, IsMyFriendStatusDeserializer())
        }.create().fromJson<UserData>(intent.getStringExtra("USER_DATA"), object  : TypeToken<UserData>() {}.type)

        when(intent.action?.toNotificationAction()){
            NotificationAction.ACCEPT_FRIENDSHIP ->{

                GlobalScope.launch {
                    UsersModule.getUsersInteractorImpl().acceptFriendship(
                        FriendActionRequestData(
                            UserUtils.getUserIDTokenData()!!,
                            userData.userId
                        )
                    )
                }
                UserUtils.isMyFriendLiveData.value = userData.apply {
                    isMyFriendStatus = IsMyFriendStatus.Success.IsMyFriend(userData.userId)
                }
            }
            NotificationAction.REJECT_FRIENDSHIP -> {
                GlobalScope.launch {
                    UsersModule.getUsersInteractorImpl().rejectFriendship(
                        FriendActionRequestData(
                            UserUtils.getUserIDTokenData()!!,
                            userData.userId
                        )
                    )
                }
                UserUtils.isMyFriendLiveData.value = userData.apply {
                    isMyFriendStatus = IsMyFriendStatus.Success.IsNotMyFriend(userData.userId)
                }
            }
        }

        if(UserUtils.userDataLiveData.value != null) {
            UserUtils.userDataLiveData.postValue(UserUtils.userData.apply {
                notificationsAreChecked = true
            })
        }
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(notificationId)
    }

    enum class NotificationAction{
        ACCEPT_FRIENDSHIP,
        REJECT_FRIENDSHIP
    }

}

fun String.toNotificationAction() = when(this){
    "ACCEPT_FRIENDSHIP" -> NotificationsActionsHandleBroadcastReceiver.NotificationAction.ACCEPT_FRIENDSHIP
    "REJECT_FRIENDSHIP" -> NotificationsActionsHandleBroadcastReceiver.NotificationAction.REJECT_FRIENDSHIP
    else -> throw UnsupportedOperationException("unknown notifications action")
}
fun NotificationsActionsHandleBroadcastReceiver.NotificationAction.toActionString() = when(this){
    NotificationsActionsHandleBroadcastReceiver.NotificationAction.ACCEPT_FRIENDSHIP -> "ACCEPT_FRIENDSHIP"
    NotificationsActionsHandleBroadcastReceiver.NotificationAction.REJECT_FRIENDSHIP -> "REJECT_FRIENDSHIP"
}