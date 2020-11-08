package com.studita.presentation.adapter.notifications

import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.studita.presentation.model.NotificationsUiModel
import com.studita.utils.PrefsUtils
import com.studita.utils.dpToPx
import kotlinx.android.synthetic.main.notifications_layout_switch_item.view.*

class NotificationsSwitchViewHolder(view: View) :
    NotificationsViewHolder<NotificationsUiModel.NotificationsSwitch>(view) {

    override fun bind(model: NotificationsUiModel) {
        model as NotificationsUiModel.NotificationsSwitch

        with(itemView) {
            notificationsLayoutSwitchItemSwitch.isChecked = PrefsUtils.notificationsAreEnabled()
            this.setOnClickListener {
                notificationsLayoutSwitchItemSwitch.isChecked =
                    !notificationsLayoutSwitchItemSwitch.isChecked
                PrefsUtils.setNotificationsMode(notificationsLayoutSwitchItemSwitch.isChecked)
            }
            updateLayoutParams<ViewGroup.MarginLayoutParams> {
                this.bottomMargin = 12F.dpToPx()
            }
        }
    }

}