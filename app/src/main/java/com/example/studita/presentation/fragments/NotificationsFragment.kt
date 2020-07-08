package com.example.studita.presentation.fragments

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
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.entity.NotificationData
import com.example.studita.domain.entity.serializer.IsMyFriendStatusDeserializer
import com.example.studita.domain.interactor.IsMyFriendStatus
import com.example.studita.notifications.service.PushReceiverIntentService
import com.example.studita.presentation.adapter.notifications.NotificationsAdapter
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.model.NotificationsUiModel
import com.example.studita.presentation.model.toUiModel
import com.example.studita.presentation.view_model.NotificationsFragmentViewModel
import com.example.studita.utils.UserUtils
import com.example.studita.utils.dpToPx
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.recyclerview_layout.*
import java.lang.Exception

class NotificationsFragment : NavigatableFragment(R.layout.recyclerview_layout){

    lateinit var viewModel: NotificationsFragmentViewModel

    private val notificationReceiver: BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            val notificationData = GsonBuilder().apply {
                registerTypeAdapter(IsMyFriendStatus::class.java, IsMyFriendStatusDeserializer())
            }.create().fromJson<NotificationData>(
                intent.getStringExtra("NOTIFICATION_DATA"),
                object : TypeToken<NotificationData>() {}.type
            )

            viewModel.recyclerItems?.add(1, notificationData.toUiModel(context))
            recyclerViewLayoutRecyclerView.adapter?.notifyItemInserted(1)

            if(isVisible)
                resultCode = Activity.RESULT_CANCELED
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProviders.of(this).get(NotificationsFragmentViewModel::class.java)

        recyclerViewLayoutRecyclerView.setPadding(0, resources.getDimension(R.dimen.toolbarHeight).toInt() + 12.dpToPx(), 0, 12.dpToPx())

        viewModel.notificationsState.observe(
            viewLifecycleOwner,
            Observer { pair->

                val canBeMoreItems = pair.first

                when (val notificationsResultState = pair.second) {
                    is NotificationsFragmentViewModel.NotificationsResultState.FirstResults -> {

                        if (recyclerViewLayoutRecyclerView.adapter == null) {
                            val items =
                                if (viewModel.recyclerItems != null) viewModel.recyclerItems!! else ArrayList(
                                    viewModel.getRecyclerItems(
                                        NotificationsUiModel.NotificationsSwitch,
                                        notificationsResultState.results,
                                        if(canBeMoreItems) NotificationsUiModel.ProgressUiModel else null,
                                        view.context
                                    )
                                )
                            val adapter = NotificationsAdapter(
                                items,
                                viewModel
                            )
                            recyclerViewLayoutRecyclerView.adapter = adapter
                            viewModel.recyclerItems = adapter.items
                        } else {
                            val adapter =
                                recyclerViewLayoutRecyclerView.adapter as NotificationsAdapter
                            val newData = viewModel.getRecyclerItems(
                                NotificationsUiModel.NotificationsSwitch,
                                notificationsResultState.results,
                                if(canBeMoreItems) NotificationsUiModel.ProgressUiModel else null,
                                view.context
                            )
                            adapter.items.addAll(newData)
                            adapter.notifyItemRangeInserted(0, newData.size)
                        }
                    }
                    is NotificationsFragmentViewModel.NotificationsResultState.MoreResults -> {

                        if (recyclerViewLayoutRecyclerView.adapter != null) {
                            val items = notificationsResultState.results.map { it.toUiModel(view.context) }
                            val adapter =
                                recyclerViewLayoutRecyclerView.adapter as NotificationsAdapter

                            val insertIndex = adapter.itemCount - 1
                            adapter.items.addAll(insertIndex, items)
                            adapter.notifyItemRangeInserted(
                                insertIndex,
                                items.size
                            )
                            if(!canBeMoreItems){
                                adapter.items.removeAll{it is NotificationsUiModel.ProgressUiModel}
                                adapter.notifyItemRemoved(adapter.itemCount-1)
                            }
                        } else {
                            val adapter = NotificationsAdapter(
                                viewModel.recyclerItems!!,
                                viewModel
                            )
                            recyclerViewLayoutRecyclerView.adapter = adapter
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
            })
        
        viewModel.progressState.observe(
            viewLifecycleOwner,
            Observer { showProgress ->
                recyclerViewLayoutProgressBar.visibility = if(showProgress) View.VISIBLE else {
                    registerReceiver()
                    (view.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancelAll()
                    View.GONE
                }
            })

        UserUtils.isMyFriendLiveData.observe(viewLifecycleOwner, Observer { friendData ->

                viewModel.recyclerItems?.forEachIndexed { index, notificationsUiModel ->
                    if((notificationsUiModel is NotificationsUiModel.Notification) && notificationsUiModel.userData.userId == friendData.userId){
                        (viewModel.recyclerItems?.get(index) as NotificationsUiModel.Notification).userData.isMyFriendStatus = friendData.isMyFriendStatus
                        recyclerViewLayoutRecyclerView.adapter?.notifyItemChanged(index, Unit)
                    }
                }
        })

        scrollingView = recyclerViewLayoutRecyclerView
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            activity?.unregisterReceiver(notificationReceiver)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun showEmptyView(){
        recyclerViewLayoutRecyclerView.adapter = NotificationsAdapter(arrayListOf(NotificationsUiModel.NotificationsSwitch), null)
        (view as ViewGroup).addView(TextView(context).apply {
            TextViewCompat.setTextAppearance(this, R.style.Regular16Secondary)
            text = resources.getString(R.string.notifications_are_empty)

            post{
                updateLayoutParams {
                    gravity = Gravity.CENTER
                }
            }
        })
    }

    private fun registerReceiver(){
        try {
            val filter = IntentFilter().apply {
                addAction(PushReceiverIntentService.BROADCAST_NOTIFICATION)
                priority = 1
            }
            activity?.registerReceiver(notificationReceiver, filter)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

}