package com.example.studita.presentation.model

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.core.content.res.ResourcesCompat
import com.example.studita.R
import com.example.studita.domain.entity.NotificationData
import com.example.studita.domain.entity.NotificationType
import com.example.studita.domain.entity.UserData
import com.example.studita.domain.entity.toStatus
import com.example.studita.utils.TimeUtils.secondsToAgoString
import com.example.studita.utils.createSpannableString

sealed class NotificationsUiModel {

    object NotificationsSwitch : NotificationsUiModel()
    class Notification(
        val userData: UserData,
        val notificationText: SpannableStringBuilder,
        val timeText: String,
        val notificationType: NotificationType
    ) : NotificationsUiModel()

    object ProgressUiModel : NotificationsUiModel()

}

fun NotificationData.toUiModel(context: Context) = NotificationsUiModel.Notification(
    UserData(userId, userName, avatarLink, isMyFriendData.toStatus(userId)),
    createNotificationText(context),
    secondsAgo.secondsToAgoString(context),
    notificationType
)

fun NotificationData.createNotificationText(context: Context): SpannableStringBuilder {
    val builder = SpannableStringBuilder()
    builder.append(
        context.resources.getString(R.string.user_name_template, userName).createSpannableString(
            typeFace = ResourcesCompat.getFont(
                context,
                R.font.roboto_medium
            )
        )
    )
    builder.append(" ")
    builder.append(
        context.resources.getString(
            when (notificationType) {
                NotificationType.DUEL_REQUEST -> R.string.notification_type_duel_invite_text
                NotificationType.FRIENDSHIP_REQUEST -> R.string.notification_type_request_friendship
                NotificationType.ACCEPTED_FRIENDSHIP -> R.string.notification_type_accepted_friendship
            }
        )
    )
    return builder
}