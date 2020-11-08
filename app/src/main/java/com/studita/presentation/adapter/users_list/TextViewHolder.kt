package com.studita.presentation.adapter.users_list

import android.view.View
import com.studita.presentation.model.UsersRecyclerUiModel
import kotlinx.android.synthetic.main.friends_search_text_item.view.*

class TextViewHolder(view: View) : UsersViewHolder<UsersRecyclerUiModel.TextItemUiModel>(view) {
    override fun bind(model: UsersRecyclerUiModel) {
        model as UsersRecyclerUiModel.TextItemUiModel
        itemView.friendsSearchTextItemText.text = model.text
    }

}
