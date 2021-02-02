package com.studita.presentation.adapter.notifications

import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.studita.R
import com.studita.domain.entity.NotificationType
import com.studita.domain.interactor.IsMyFriendStatus
import com.studita.presentation.fragments.profile.ProfileFragment
import com.studita.presentation.fragments.dialog_alerts.AcceptFriendshipDialogAlertFragment
import com.studita.presentation.listeners.OnSingleClickListener.Companion.setOnSingleClickListener
import com.studita.presentation.model.NotificationsUiModel
import com.studita.presentation.fragments.achievements.AchievementsFragment
import com.studita.utils.*
import kotlinx.android.synthetic.main.notifications_layout_item.view.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class NotificationViewHolder(view: View, private val lastNotificationCheckCallback: LastNotificationCheckCallback) :
    NotificationsViewHolder<NotificationsUiModel>(view) {

    override fun bind(model: NotificationsUiModel) {

            if (absoluteAdapterPosition == 1) {
                lastNotificationCheckCallback.onLastNotificationCheck()
            }

        when (model) {
            is NotificationsUiModel.NotificationFromUser -> {
                with(itemView) {
                    notificationsLayoutItemAvatar.fillAvatar(
                        model.userData.avatarLink,
                        model.userData.userName,
                        model.userData.userId
                    )
                    notificationsLayoutItemTitle.text = model.notificationText
                    notificationsLayoutItemSubtitle.text = model.timeText

                    when (model.notificationType) {
                        NotificationType.AcceptedFriendship -> this.setOnClickListener {
                            this.getAppCompatActivity()
                                ?.let { it1 -> navigateToProfile(it1, model.userData.userId) }
                        }
                        NotificationType.DuelRequest -> {
                        }
                        NotificationType.FriendshipRequest -> {
                            if (model.userData.isMyFriendStatus is IsMyFriendStatus.Success.WaitingForFriendshipAccept) {
                                this.setOnSingleClickListener {
                                    this.getAppCompatActivity()?.supportFragmentManager?.let {
                                        AcceptFriendshipDialogAlertFragment().apply {
                                            arguments =
                                                bundleOf("USER_DATA" to Json.encodeToString(model.userData))
                                        }.show(it, null)
                                    }
                                }
                            } else {
                                this.setOnClickListener {
                                    this.getAppCompatActivity()
                                        ?.let { it1 -> navigateToProfile(it1, model.userData.userId) }
                                }
                            }
                        }
                    }
                }
            }
            is NotificationsUiModel.AchievementNotification -> {
                with(itemView) {
                    notificationsLayoutItemAvatar.loadSVG(model.iconLink, R.drawable.avatar_placeholder)
                    notificationsLayoutItemTitle.text = model.notificationText
                    notificationsLayoutItemSubtitle.text = model.timeText
                    this.setOnClickListener {
                        this.getAppCompatActivity()
                            ?.navigateTo(AchievementsFragment().apply {
                                                                      arguments = bundleOf("NOTIFICATION_TYPE_SELECTION" to Json.encodeToString(model.notificationType.type))
                            }, R.id.doubleFrameLayoutFrameLayout)
                    }
                }
            }
        }
    }

    private fun navigateToProfile(activity: AppCompatActivity, userId: Int) {
        activity.navigateTo(
            ProfileFragment().apply {
            arguments = bundleOf("USER_ID" to userId)
        }, R.id.doubleFrameLayoutFrameLayout)
    }

    interface LastNotificationCheckCallback{
        fun onLastNotificationCheck()
    }

}