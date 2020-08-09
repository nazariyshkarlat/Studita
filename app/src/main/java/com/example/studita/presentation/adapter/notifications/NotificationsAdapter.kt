package com.example.studita.presentation.adapter.notifications

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studita.R
import com.example.studita.presentation.model.NotificationsUiModel
import com.example.studita.presentation.view_model.NotificationsFragmentViewModel
import com.example.studita.utils.UserUtils
import com.example.studita.utils.makeView

class NotificationsAdapter(
    val items: ArrayList<NotificationsUiModel>,
    private val notificationsFragmentViewModel: NotificationsFragmentViewModel?
) :
    RecyclerView.Adapter<NotificationsViewHolder<*>>(), LoadViewHolder.RequestMoreItems {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationsViewHolder<out NotificationsUiModel> = when (viewType) {
        NotificationsViewType.SWITCH.ordinal -> NotificationsSwitchViewHolder(parent.makeView(R.layout.notifications_layout_switch_item))
        NotificationsViewType.NOTIFICATION.ordinal -> NotificationViewHolder(parent.makeView(R.layout.notifications_layout_item))
        NotificationsViewType.ITEMS_LOAD.ordinal -> LoadViewHolder(
            parent.makeView(R.layout.friends_load_item),
            this
        )
        else -> throw UnsupportedOperationException("unknown type of item")
    }

    override fun onBindViewHolder(
        holder: NotificationsViewHolder<out NotificationsUiModel>,
        position: Int
    ) {
        holder.bind(items[position])
    }

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is NotificationsUiModel.NotificationsSwitch -> NotificationsViewType.SWITCH.ordinal
            is NotificationsUiModel.Notification -> NotificationsViewType.NOTIFICATION.ordinal
            is NotificationsUiModel.ProgressUiModel -> NotificationsViewType.ITEMS_LOAD.ordinal
        }

    override fun getItemCount() = items.size

    override fun onRequestMoreItems() {
        notificationsFragmentViewModel?.getNotifications(UserUtils.getUserIDTokenData()!!, true)
    }

}


abstract class NotificationsViewHolder<T : NotificationsUiModel>(view: View) :
    RecyclerView.ViewHolder(view) {

    abstract fun bind(model: NotificationsUiModel)
}

private enum class NotificationsViewType {
    SWITCH,
    NOTIFICATION,
    ITEMS_LOAD
}