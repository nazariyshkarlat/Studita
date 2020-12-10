package com.studita.presentation.adapter.notifications

import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.studita.R
import com.studita.domain.entity.NotificationType
import com.studita.domain.entity.serializer.IsMyFriendStatusSerializer
import com.studita.domain.interactor.IsMyFriendStatus
import com.studita.presentation.fragments.profile.ProfileFragment
import com.studita.presentation.fragments.dialog_alerts.AcceptFriendshipDialogAlertFragment
import com.studita.presentation.listeners.OnSingleClickListener.Companion.setOnSingleClickListener
import com.studita.presentation.model.NotificationsUiModel
import com.studita.utils.UserUtils
import com.studita.utils.fillAvatar
import com.studita.utils.getAppCompatActivity
import com.studita.utils.navigateTo
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.notifications_layout_item.view.*

class NotificationViewHolder(view: View, private val lastNotificationCheckCallback: LastNotificationCheckCallback) :
    NotificationsViewHolder<NotificationsUiModel.Notification>(view) {

    override fun bind(model: NotificationsUiModel) {
        model as NotificationsUiModel.Notification

        if(adapterPosition == 1){
            lastNotificationCheckCallback.onLastNotificationCheck()
        }

        with(itemView) {
            notificationsLayoutItemAvatar.fillAvatar(
                model.userData.avatarLink,
                model.userData.userName,
                model.userData.userId
            )
            notificationsLayoutItemTitle.text = model.notificationText
            notificationsLayoutItemSubtitle.text = model.timeText

            when (model.notificationType) {
                NotificationType.ACCEPTED_FRIENDSHIP -> this.setOnClickListener {
                    this.getAppCompatActivity()
                        ?.let { it1 -> navigateToProfile(it1, model.userData.userId) }
                }
                NotificationType.DUEL_REQUEST -> {
                }
                NotificationType.FRIENDSHIP_REQUEST -> {
                    if (model.userData.isMyFriendStatus is IsMyFriendStatus.Success.WaitingForFriendshipAccept) {
                        this.setOnSingleClickListener {
                            this.getAppCompatActivity()?.supportFragmentManager?.let {
                                AcceptFriendshipDialogAlertFragment().apply {
                                    arguments =
                                        bundleOf("USER_DATA" to GsonBuilder().apply {
                                            registerTypeAdapter(
                                                IsMyFriendStatus.Success::class.java,
                                                IsMyFriendStatusSerializer()
                                            )
                                        }.create().toJson(model.userData))
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