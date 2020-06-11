package com.example.studita.presentation.adapter.users_list

import android.view.View
import com.example.studita.presentation.model.UsersRecyclerUiModel

class LoadViewHolder(view: View, private val requestMoreItems: RequestMoreItems) : FriendsViewHolder<UsersRecyclerUiModel.SearchUiModel>(view){
    override fun bind(model: UsersRecyclerUiModel) {
        requestMoreItems.onRequestMoreItems()
    }

    interface RequestMoreItems{

        fun onRequestMoreItems()

    }

}
