package com.example.studita.presentation.adapter.friends

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studita.R
import com.example.studita.presentation.model.FriendsRecyclerUiModel
import com.example.studita.presentation.model.HomeRecyclerUiModel
import com.example.studita.presentation.view_model.FriendsFragmentViewModel
import com.example.studita.utils.makeView
import com.example.studita.presentation.view_model.HomeFragmentViewModel
import java.lang.UnsupportedOperationException

class FriendsAdapter(val items: ArrayList<FriendsRecyclerUiModel>, private val friendsFragmentViewModel: FriendsFragmentViewModel) :
    RecyclerView.Adapter<FriendsViewHolder<*>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder<out FriendsRecyclerUiModel> = when (viewType) {
        ViewType.SEARCH.ordinal -> SearchViewHolder(parent.makeView(R.layout.friends_search_item))
        ViewType.ITEM.ordinal -> FriendItemViewHolder(parent.makeView(R.layout.friend_item), friendsFragmentViewModel)
        else -> throw UnsupportedOperationException("unknown type of item")
    }

    override fun onBindViewHolder(holder: FriendsViewHolder<out FriendsRecyclerUiModel>, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is FriendsRecyclerUiModel.SearchUiModel -> ViewType.SEARCH.ordinal
            is FriendsRecyclerUiModel.FriendItemUiModel -> ViewType.ITEM.ordinal
        }

    override fun getItemCount() = items.size

}


abstract class FriendsViewHolder<T : FriendsRecyclerUiModel>(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(model: FriendsRecyclerUiModel)
}

private enum class ViewType {
    SEARCH,
    ITEM
}