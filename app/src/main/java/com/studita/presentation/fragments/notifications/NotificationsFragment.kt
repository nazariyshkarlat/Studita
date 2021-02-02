package com.studita.presentation.fragments.notifications

import android.app.Activity
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.contains
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.studita.R
import com.studita.domain.entity.NotificationData
import com.studita.domain.entity.NotificationType
import com.studita.domain.entity.UserData
import com.studita.domain.interactor.IsMyFriendStatus
import com.studita.domain.interactor.users.UsersInteractor
import com.studita.notifications.service.PushReceiverIntentService
import com.studita.presentation.adapter.notifications.NotificationsAdapter
import com.studita.presentation.fragments.base.NavigatableFragment
import com.studita.presentation.fragments.error_fragments.InternetIsDisabledFragment
import com.studita.presentation.fragments.error_fragments.ServerProblemsFragment
import com.studita.presentation.listeners.ReloadPageCallback
import com.studita.presentation.model.NotificationsUiModel
import com.studita.presentation.view_model.NotificationsFragmentViewModel
import com.studita.utils.UserUtils
import com.studita.utils.addFragment
import com.studita.data.entity.isNotificationType
import com.studita.data.entity.toFirebaseMessageType
import com.studita.data.entity.toNotificationType
import com.studita.domain.entity.MessageType
import com.studita.notifications.service.MessageReceiverIntentService.Companion.BROADCAST_MESSAGE
import com.studita.presentation.model.toUiModel
import com.studita.utils.dp
import kotlinx.android.synthetic.main.recyclerview_layout.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.lang.UnsupportedOperationException

class NotificationsFragment : NavigatableFragment(R.layout.recyclerview_layout),
    ReloadPageCallback {

    val viewModel: NotificationsFragmentViewModel by lazy {
        ViewModelProviders.of(this).get(NotificationsFragmentViewModel::class.java)
    }

    private val emptyView: View by lazy { TextView(context).apply {
        TextViewCompat.setTextAppearance(this, R.style.Regular16Secondary)
        text = resources.getString(R.string.notifications_are_empty)

        layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER
        }
    }
    }

    private val notificationReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            val type = intent.getStringExtra("type")!!
                if (type.isNotificationType()) {
                    if(type.toNotificationType() !is NotificationType.Achievement) {

                        val notificationData:NotificationData.NotificationFromUser  = Json.decodeFromString(intent.getStringExtra("NOTIFICATION_DATA")!!)
                        when (notificationData.notificationType) {
                            NotificationType.FriendshipRequest -> {
                                UserUtils.isMyFriendLiveData.postValue(
                                    UsersInteractor.FriendActionState.FriendshipRequestIsSent(
                                        UserData(
                                            notificationData.userId,
                                            notificationData.userName,
                                            notificationData.imageLink,
                                            IsMyFriendStatus.Success.WaitingForFriendshipAccept(
                                                notificationData.userId
                                            )
                                        )
                                    )
                                )
                            }
                            NotificationType.AcceptedFriendship -> {
                                UserUtils.isMyFriendLiveData.postValue(
                                    UsersInteractor.FriendActionState.FriendshipRequestIsAccepted(
                                        UserData(
                                            notificationData.userId,
                                            notificationData.userName,
                                            notificationData.imageLink,
                                            IsMyFriendStatus.Success.IsMyFriend(notificationData.userId)
                                        )
                                    )
                                )
                            }
                            else -> {
                                throw UnsupportedOperationException("unknown image type for NotificaionFromUser")
                            }
                        }
                        viewModel.recyclerItems?.add(1, notificationData.toUiModel(context))
                    }else{
                        val notificationData = Json.decodeFromString<NotificationData.AchievementNotification>(intent.getStringExtra("NOTIFICATION_DATA")!!)
                        viewModel.recyclerItems?.add(1, notificationData.toUiModel(context))
                    }

                recyclerViewLayoutRecyclerView.adapter?.notifyItemInserted(1)

                viewModel.notificationsState.value = false to NotificationsFragmentViewModel.NotificationsResultState.MoreResults(
                    emptyList())


                val maxScrollWithoutPush = resources.getDimension(R.dimen.toolbarHeight)
                if (!isHidden && (recyclerViewLayoutRecyclerView.computeVerticalScrollOffset() < maxScrollWithoutPush)) {
                    resultCode = Activity.RESULT_CANCELED

                    UserUtils.userDataLiveData.value = UserUtils.userData.apply {
                        notificationsAreChecked = false
                    }

                    (view?.context?.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.cancelAll()
                }
            }else{
                when(type.toFirebaseMessageType()){
                    MessageType.FRIENDSHIP_REQUEST_CANCELLED, MessageType.FRIENDSHIP_REMOVED -> {
                        val elementsToRemove = ArrayList<NotificationsUiModel>()
                        viewModel.recyclerItems?.forEach {notificationsUiModel ->
                            if((notificationsUiModel is NotificationsUiModel.NotificationFromUser) &&
                                ((notificationsUiModel.notificationType == NotificationType.FriendshipRequest)
                                        && (notificationsUiModel.userData.userId == intent.getIntExtra("user_id", 0)))){
                                elementsToRemove.add(notificationsUiModel)
                            }
                        }
                        elementsToRemove.forEach {
                            val removeIndex = viewModel.recyclerItems!!.indexOf(it)
                            viewModel.recyclerItems?.remove(it)
                            recyclerViewLayoutRecyclerView.adapter?.notifyItemRemoved(removeIndex)
                        }
                        if(viewModel.recyclerItems?.count { it is NotificationsUiModel.NotificationFromUser } == 0){
                            viewModel.notificationsState.value = false to NotificationsFragmentViewModel.NotificationsResultState.NoResultsFound
                        }
                    }
                }
            }
        }

    }

    fun scrollRecyclerToTop(){
        recyclerViewLayoutRecyclerView.smoothScrollToPosition(0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewLayoutRecyclerView.isSaveEnabled = false

        recyclerViewLayoutRecyclerView.setPadding(
            0,
            resources.getDimension(R.dimen.toolbarHeight).toInt() + 12F.dp,
            0,
            12F.dp
        )

        viewModel.notificationsState.observe(
            viewLifecycleOwner,
            Observer { pair ->

                val canBeMoreItems = pair.first

                when (val notificationsResultState = pair.second) {
                    is NotificationsFragmentViewModel.NotificationsResultState.FirstResults -> {

                        val items =
                            if (viewModel.recyclerItems != null) viewModel.recyclerItems!! else ArrayList(
                                viewModel.getRecyclerItems(
                                    NotificationsUiModel.NotificationsSwitch,
                                    notificationsResultState.results,
                                    if (canBeMoreItems) NotificationsUiModel.ProgressUiModel else null,
                                    view.context
                                )
                            )
                        val adapter = NotificationsAdapter(
                            context!!,
                            items,
                            viewModel
                        )
                        recyclerViewLayoutRecyclerView.adapter = adapter
                        viewModel.recyclerItems = adapter.items

                    }
                    is NotificationsFragmentViewModel.NotificationsResultState.MoreResults -> {

                        if(!notificationsResultState.results.isNullOrEmpty()) {
                            if (recyclerViewLayoutRecyclerView.adapter != null) {

                                val items = listOf(
                                    *notificationsResultState.results.map { it.toUiModel(view.context) }
                                        .toTypedArray(),
                                    *(if (canBeMoreItems) arrayOf(NotificationsUiModel.ProgressUiModel) else emptyArray())
                                )

                                val adapter =
                                    recyclerViewLayoutRecyclerView.adapter as NotificationsAdapter

                                val removePos = adapter.items.lastIndex
                                adapter.items.removeAt(removePos)
                                adapter.notifyItemRemoved(removePos)

                                val insertIndex = adapter.items.size
                                adapter.items.addAll(items)
                                adapter.notifyItemRangeInserted(
                                    insertIndex,
                                    items.size
                                )
                            } else {
                                val adapter = NotificationsAdapter(
                                    context!!,
                                    viewModel.recyclerItems!!,
                                    viewModel
                                )
                                recyclerViewLayoutRecyclerView.adapter = adapter
                            }
                        }
                    }
                    is NotificationsFragmentViewModel.NotificationsResultState.NoMoreResultsFound -> {

                        if (recyclerViewLayoutRecyclerView.adapter != null) {
                            val progressIndex =
                                viewModel.recyclerItems?.indexOfFirst { it is NotificationsUiModel.ProgressUiModel }
                            if (progressIndex != null && progressIndex != -1) {
                                viewModel.recyclerItems?.removeAt(progressIndex)
                                recyclerViewLayoutRecyclerView.adapter?.notifyItemRemoved(
                                    progressIndex
                                )
                            }
                        } else {
                            val adapter = NotificationsAdapter(
                                context!!,
                                viewModel.recyclerItems!!,
                                viewModel
                            )
                            recyclerViewLayoutRecyclerView.adapter = adapter
                        }
                    }
                    is NotificationsFragmentViewModel.NotificationsResultState.NoResultsFound -> {
                        showEmptyView()
                    }
                }
                if(pair.second !is NotificationsFragmentViewModel.NotificationsResultState.NoResultsFound &&
                    (view as ViewGroup).contains(emptyView)){
                    view.removeView(emptyView)
                }
            })

        viewModel.progressState.observe(
            viewLifecycleOwner,
            Observer { showProgress ->
                recyclerViewLayoutProgressBar.visibility = if (showProgress) View.VISIBLE else {
                    registerReceiver()
                    (view.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancelAll()
                    View.GONE
                }
            })

        viewModel.errorEvent.observe(viewLifecycleOwner, Observer { isNetworkError->
            clearToolbarDivider()
            if (isNetworkError) {
                addFragment(InternetIsDisabledFragment(), R.id.recyclerViewLayoutFrameLayout, false)
            }else{
                addFragment(ServerProblemsFragment(), R.id.recyclerViewLayoutFrameLayout, false)
            }
        })

        UserUtils.isMyFriendLiveData.observe(viewLifecycleOwner, Observer {
            viewModel.recyclerItems?.forEachIndexed { index, notificationsUiModel ->
                if ((notificationsUiModel is NotificationsUiModel.NotificationFromUser) && notificationsUiModel.userData.userId == it.userData.userId) {
                    (viewModel.recyclerItems?.get(index) as NotificationsUiModel.NotificationFromUser).userData.isMyFriendStatus =
                        it.userData.isMyFriendStatus
                    recyclerViewLayoutRecyclerView.adapter?.notifyItemChanged(index, Unit)
                }
            }
        })

        scrollingView = recyclerViewLayoutRecyclerView

        recyclerViewLayoutRecyclerView.post {
            if (viewModel.errorState)
                toolbarFragmentViewModel?.hideDivider()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            activity?.unregisterReceiver(notificationReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showEmptyView() {
        viewModel.recyclerItems =
            arrayListOf(NotificationsUiModel.NotificationsSwitch)
        recyclerViewLayoutRecyclerView.adapter =
            NotificationsAdapter(context!!,
                viewModel.recyclerItems!!,
                null)
        if(!(view as ViewGroup).contains(emptyView))
                (view as ViewGroup).addView(emptyView)
    }


    private fun registerReceiver() {
        try {
            val filter = IntentFilter().apply {
                addAction(PushReceiverIntentService.BROADCAST_NOTIFICATION)
                addAction(BROADCAST_MESSAGE)
                priority = 1
            }
            activity?.registerReceiver(notificationReceiver, filter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if(!isHidden) {
            if (viewModel.errorState)
                toolbarFragmentViewModel?.hideDivider()
            else
                super.onHiddenChanged(hidden)
        }
    }

    override fun onScrollChanged() {
        if(!viewModel.errorState)
            super.onScrollChanged()
    }

    override fun onPageReload() {
        checkScroll()
        viewModel.getNotifications(UserUtils.getUserIDTokenData()!!, false)
    }

    private fun clearToolbarDivider(){
        toolbarFragmentViewModel?.hideDivider()
    }

}