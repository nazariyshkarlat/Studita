package com.studita.presentation.model

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.core.content.res.ResourcesCompat
import com.studita.R
import com.studita.domain.entity.NotificationData
import com.studita.domain.entity.NotificationType
import com.studita.domain.entity.UserData
import com.studita.domain.entity.toStatus
import com.studita.utils.TimeUtils.secondsToAgoString
import com.studita.utils.createSpannableString
import java.lang.Character.OTHER_SYMBOL
import java.lang.UnsupportedOperationException

sealed class NotificationsUiModel {

    object NotificationsSwitch : NotificationsUiModel()
    class NotificationFromUser(
        val userData: UserData,
        val notificationText: SpannableStringBuilder,
        val timeText: String,
        val notificationType: NotificationType
    ) : NotificationsUiModel()

    class AchievementNotification(
        val iconLink: String,
        val notificationText: String,
        val timeText: String,
        val notificationType: NotificationType.Achievement,
    ) : NotificationsUiModel()

    object ProgressUiModel : NotificationsUiModel()

}

fun NotificationData.toUiModel(context: Context) =
    when (this) {
        is NotificationData.NotificationFromUser -> {
            NotificationsUiModel.NotificationFromUser(
                UserData(userId, userName, imageLink, isMyFriendData.toStatus(userId)),
                createNotificationText(context),
                secondsAgo.secondsToAgoString(context),
                notificationType
            )
        }
        is NotificationData.AchievementNotification -> {
            NotificationsUiModel.AchievementNotification(
                iconLink,
                "$title.${subtitle.filter {it.category != CharCategory.SURROGATE}}",
                secondsAgo.secondsToAgoString(context),
                notificationType
            )
        }
    }

fun NotificationData.NotificationFromUser.createNotificationText(context: Context): SpannableStringBuilder {
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
                NotificationType.DuelRequest -> R.string.notification_type_duel_invite_text
                NotificationType.FriendshipRequest -> R.string.notification_type_request_friendship
                NotificationType.AcceptedFriendship -> R.string.notification_type_accepted_friendship
                else -> throw UnsupportedOperationException("unknown notification type")
            }
        )
    )
    return builder
}