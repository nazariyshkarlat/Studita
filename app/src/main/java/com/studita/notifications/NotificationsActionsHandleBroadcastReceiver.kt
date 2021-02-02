package com.studita.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.studita.domain.entity.FriendActionRequestData
import com.studita.domain.entity.UserData
import com.studita.domain.interactor.IsMyFriendStatus
import com.studita.domain.interactor.users.UsersInteractor
import com.studita.utils.UserUtils
import com.studita.domain.interactor.notifications.NotificationsInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.core.context.GlobalContext


class NotificationsActionsHandleBroadcastReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", 0)
        val userData = Json.decodeFromString<UserData>(
            intent.getStringExtra("USER_DATA")!!
        )

        when (intent.action?.toNotificationAction()) {
            NotificationAction.ACCEPT_FRIENDSHIP -> {

                GlobalScope.launch(Dispatchers.Main) {
                    GlobalContext.get().get<UsersInteractor>().acceptFriendship(
                        FriendActionRequestData(
                            UserUtils.getUserIDTokenData()!!,
                            userData.userId
                        )
                    )
                    GlobalContext.get().get<NotificationsInteractor>().setNotificationsAreChecked(UserUtils.getUserIDTokenData()!!)
                }
                UserUtils.isMyFriendLiveData.value =
                    UsersInteractor.FriendActionState.FriendshipRequestIsAccepted(userData.apply {
                        isMyFriendStatus = IsMyFriendStatus.Success.IsMyFriend(userData.userId)
                    })
            }
            NotificationAction.REJECT_FRIENDSHIP -> {
                GlobalScope.launch(Dispatchers.Main) {
                    GlobalContext.get().get<UsersInteractor>().rejectFriendship(
                        FriendActionRequestData(
                            UserUtils.getUserIDTokenData()!!,
                            userData.userId
                        )
                    )
                    GlobalContext.get().get<NotificationsInteractor>().setNotificationsAreChecked(UserUtils.getUserIDTokenData()!!)
                }
                UserUtils.isMyFriendLiveData.value =
                    UsersInteractor.FriendActionState.FriendshipRequestIsRejected(userData.apply {
                        isMyFriendStatus = IsMyFriendStatus.Success.IsNotMyFriend(userData.userId)
                    })
            }
        }

        if (UserUtils.userDataNotNull()) {
            UserUtils.userDataLiveData.value = UserUtils.userData.apply {
                notificationsAreChecked = true
            }
        }
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(
            notificationId
        )
    }

    enum class NotificationAction {
        ACCEPT_FRIENDSHIP,
        REJECT_FRIENDSHIP
    }

}

fun String.toNotificationAction() = when (this) {
    "ACCEPT_FRIENDSHIP" -> NotificationsActionsHandleBroadcastReceiver.NotificationAction.ACCEPT_FRIENDSHIP
    "REJECT_FRIENDSHIP" -> NotificationsActionsHandleBroadcastReceiver.NotificationAction.REJECT_FRIENDSHIP
    else -> throw UnsupportedOperationException("unknown notifications action")
}

fun NotificationsActionsHandleBroadcastReceiver.NotificationAction.toActionString() = when (this) {
    NotificationsActionsHandleBroadcastReceiver.NotificationAction.ACCEPT_FRIENDSHIP -> "ACCEPT_FRIENDSHIP"
    NotificationsActionsHandleBroadcastReceiver.NotificationAction.REJECT_FRIENDSHIP -> "REJECT_FRIENDSHIP"
}