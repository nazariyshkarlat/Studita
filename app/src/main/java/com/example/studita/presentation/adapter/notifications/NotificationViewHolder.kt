package com.example.studita.presentation.adapter.notifications

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.example.studita.R
import com.example.studita.domain.entity.NotificationType
import com.example.studita.domain.entity.serializer.IsMyFriendStatusSerializer
import com.example.studita.domain.interactor.IsMyFriendStatus
import com.example.studita.presentation.fragments.ProfileFragment
import com.example.studita.presentation.fragments.dialog_alerts.AcceptFriendshipDialogAlertFragment
import com.example.studita.presentation.listeners.OnSingleClickListener.Companion.setOnSingleClickListener
import com.example.studita.presentation.model.NotificationsUiModel
import com.example.studita.utils.fillAvatar
import com.example.studita.utils.getAppCompatActivity
import com.example.studita.utils.navigateTo
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.notifications_layout_item.view.*

class NotificationViewHolder(view: View) :
    NotificationsViewHolder<NotificationsUiModel.Notification>(view) {

    override fun bind(model: NotificationsUiModel) {
        model as NotificationsUiModel.Notification


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
        activity.navigateTo(ProfileFragment().apply {
            arguments = bundleOf("USER_ID" to userId)
        }, R.id.doubleFrameLayoutFrameLayout)
    }

}