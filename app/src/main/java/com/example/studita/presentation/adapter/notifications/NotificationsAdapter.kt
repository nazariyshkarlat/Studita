package com.example.studita.presentation.adapter.notifications

import android.app.NotificationManager
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studita.R
import com.example.studita.presentation.model.NotificationsUiModel
import com.example.studita.presentation.model.UsersRecyclerUiModel
import com.example.studita.presentation.view_model.NotificationsFragmentViewModel
import com.example.studita.utils.UserUtils
import com.example.studita.utils.makeView

class NotificationsAdapter(
    private val context: Context,
    val items: ArrayList<NotificationsUiModel>,
    private val notificationsFragmentViewModel: NotificationsFragmentViewModel?
) :
    RecyclerView.Adapter<NotificationsViewHolder<*>>(), NotificationViewHolder.LastNotificationCheckCallback {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationsViewHolder<out NotificationsUiModel> = when (viewType) {
        NotificationsViewType.SWITCH.ordinal -> NotificationsSwitchViewHolder(parent.makeView(R.layout.notifications_layout_switch_item))
        NotificationsViewType.NOTIFICATION.ordinal -> NotificationViewHolder(parent.makeView(R.layout.notifications_layout_item), this)
        NotificationsViewType.ITEMS_LOAD.ordinal -> LoadViewHolder(
            parent.makeView(R.layout.list_load_item)
        )
        else -> throw UnsupportedOperationException("unknown type of item")
    }

    override fun onBindViewHolder(
        holder: NotificationsViewHolder<out NotificationsUiModel>,
        position: Int
    ) {

        if(notificationsFragmentViewModel != null) {
            if ((position % notificationsFragmentViewModel.perPage == notificationsFragmentViewModel.perPage / 2) &&
                (position+notificationsFragmentViewModel.perPage > itemCount) &&
                items.any { it is NotificationsUiModel.ProgressUiModel} )
                if(!notificationsFragmentViewModel.errorState)
                    requestMoreItems()
        }

        holder.bind(items[position])
    }

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is NotificationsUiModel.NotificationsSwitch -> NotificationsViewType.SWITCH.ordinal
            is NotificationsUiModel.Notification -> NotificationsViewType.NOTIFICATION.ordinal
            is NotificationsUiModel.ProgressUiModel -> NotificationsViewType.ITEMS_LOAD.ordinal
        }

    override fun getItemCount() = items.size

    private fun requestMoreItems() {
        notificationsFragmentViewModel?.getNotifications(UserUtils.getUserIDTokenData()!!, true)
    }

    override fun onLastNotificationCheck() {
        if(!UserUtils.userData.notificationsAreChecked) {
            notificationsFragmentViewModel?.setNotificationsAreChecked(UserUtils.getUserIDTokenData()!!)
            UserUtils.userDataLiveData.value = UserUtils.userData.apply {
                notificationsAreChecked = true
            }
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.cancelAll()
        }
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
