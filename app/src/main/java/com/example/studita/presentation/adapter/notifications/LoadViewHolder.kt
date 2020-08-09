package com.example.studita.presentation.adapter.notifications

import android.view.View
import com.example.studita.presentation.model.NotificationsUiModel

class LoadViewHolder(view: View, private val requestMoreItems: RequestMoreItems) :
    NotificationsViewHolder<NotificationsUiModel>(view) {
    override fun bind(model: NotificationsUiModel) {
        requestMoreItems.onRequestMoreItems()
    }

    interface RequestMoreItems {

        fun onRequestMoreItems()

    }

}