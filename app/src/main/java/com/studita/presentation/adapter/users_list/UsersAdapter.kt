package com.studita.presentation.adapter.users_list

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.studita.R
import com.studita.domain.repository.UsersRepository
import com.studita.presentation.model.UsersRecyclerUiModel
import com.studita.presentation.view_model.FriendsFragmentViewModel
import com.studita.presentation.view_model.ToolbarFragmentViewModel
import com.studita.utils.UserUtils
import com.studita.utils.makeView

class UsersAdapter(
    var items: ArrayList<UsersRecyclerUiModel>,
    private val context: Context,
    private val friendsFragmentViewModel: FriendsFragmentViewModel,
    private val toolbarFragmentViewModel: ToolbarFragmentViewModel,
    private val userId: Int,
    var isEmptyView: Boolean = false,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<UsersViewHolder<*>>(),
    SearchViewHolder.UpdateCallback,
    SearchViewHolder.SearchCallback,
    SearchViewHolder.ShowSearchCallback,
    UserItemViewHolder.AddToFriendsCallback {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UsersViewHolder<out UsersRecyclerUiModel> {
        return when (viewType) {
            ViewType.SEARCH.ordinal -> SearchViewHolder(
                parent.makeView(R.layout.users_search_item),
                this,
                this,
                friendsFragmentViewModel.sortBy,
                friendsFragmentViewModel.searchState,
                this,
                friendsFragmentViewModel.globalSearchOnly
            )
            ViewType.ITEM.ordinal -> UserItemViewHolder(
                parent.makeView(R.layout.friend_item),
                this,
                lifecycleOwner
            )
            ViewType.ITEMS_LOAD.ordinal -> LoadViewHolder(
                parent.makeView(R.layout.list_load_item)
            )
            ViewType.TEXT.ordinal -> TextViewHolder(parent.makeView(R.layout.friends_search_text_item))
            else -> throw UnsupportedOperationException("unknown type of item")
        }
    }

    override fun onBindViewHolder(
        holder: UsersViewHolder<out UsersRecyclerUiModel>,
        position: Int
    ) {

        if ((position % friendsFragmentViewModel.perPage == friendsFragmentViewModel.perPage / 2) &&
            (position+friendsFragmentViewModel.perPage > itemCount) &&
                items.any { it is UsersRecyclerUiModel.ProgressUiModel }) {

            if(!friendsFragmentViewModel.errorState)
                requestMoreItems()
        }

        holder.bind(
            if (!isEmptyView) items[position] else (if (position == 0) UsersRecyclerUiModel.SearchUiModel else UsersRecyclerUiModel.TextItemUiModel(
                context.resources.getString(friendsFragmentViewModel.getEmptyText())
            ))
        )
    }

    override fun getItemViewType(position: Int): Int {
        return if (!isEmptyView) {
            when (items[position]) {
                is UsersRecyclerUiModel.SearchUiModel -> ViewType.SEARCH.ordinal
                is UsersRecyclerUiModel.UserItemUiModel -> ViewType.ITEM.ordinal
                is UsersRecyclerUiModel.ProgressUiModel -> ViewType.ITEMS_LOAD.ordinal
                is UsersRecyclerUiModel.TextItemUiModel -> ViewType.TEXT.ordinal
            }
        } else {
            if (position == 0)
                ViewType.SEARCH.ordinal
            else
                ViewType.TEXT.ordinal
        }
    }

    override fun getItemCount() = if (!isEmptyView) items.size else 2

    override fun update(sortBy: UsersRepository.SortBy) {
        friendsFragmentViewModel.getUsers(userId, sortBy, false, isGlobalSearch = false)
    }

    private fun requestMoreItems() {
        val startsWith: String? = friendsFragmentViewModel.searchState.let {
            when (it) {
                is FriendsFragmentViewModel.SearchState.FriendsSearch -> it.startsWith
                is FriendsFragmentViewModel.SearchState.GlobalSearch -> it.startsWith
                else -> null
            }
        }
        friendsFragmentViewModel.getUsers(
            userId,
            if (startsWith == null) friendsFragmentViewModel.sortBy else null,
            true,
            startsWith = friendsFragmentViewModel.searchState.let {
                startsWith
            },
            isGlobalSearch = friendsFragmentViewModel.searchState is FriendsFragmentViewModel.SearchState.GlobalSearch
        )
    }

    override fun search(text: String, searchState: FriendsFragmentViewModel.SearchState) {
        if (friendsFragmentViewModel.searchState != searchState) {
            friendsFragmentViewModel.searchState = searchState
            if (text.isNotEmpty()) {
                friendsFragmentViewModel.getUsers(
                    userId,
                    sortBy = null,
                    startsWith = text,
                    isGlobalSearch = friendsFragmentViewModel.searchState is FriendsFragmentViewModel.SearchState.GlobalSearch,
                    newPage = false
                )
            } else {
                if (searchState is FriendsFragmentViewModel.SearchState.GlobalSearch) {
                    friendsFragmentViewModel.formGlobalSearchEmptySearch()
                } else if (searchState is FriendsFragmentViewModel.SearchState.FriendsSearch) {
                    friendsFragmentViewModel.getUsers(
                        userId,
                        friendsFragmentViewModel.sortBy,
                        false,
                        null,
                        false
                    )
                }
            }
        }
    }

    override fun onSearchVisibilityChanged(visible: Boolean) {
        if (visible) {
            if (friendsFragmentViewModel.searchState == FriendsFragmentViewModel.SearchState.NoSearch) {
                friendsFragmentViewModel.searchState =
                    FriendsFragmentViewModel.SearchState.FriendsSearch("")
            }
            toolbarFragmentViewModel.setToolbarText(context.resources.getString(R.string.search))
        } else {
            if(friendsFragmentViewModel.searchState != FriendsFragmentViewModel.SearchState.NoSearch)
                friendsFragmentViewModel.searchState =
                    FriendsFragmentViewModel.SearchState.NoSearch
            toolbarFragmentViewModel.setToolbarText(context.resources.getString(if (userId == UserUtils.userData.userId) R.string.my_friends else R.string.friends))
        }
    }

    override fun addFriend(friendId: Int) {
        val friendData = friendsFragmentViewModel.userData?.find { it.userId == friendId }
        if (friendData != null) {
            friendsFragmentViewModel.addToFriends(UserUtils.getUserIDTokenData()!!, friendData)
        }
    }

    override fun removeFriend(friendId: Int) {
        val friendData = friendsFragmentViewModel.userData?.find { it.userId == friendId }
        if (friendData != null) {
            friendsFragmentViewModel.removeFromFriends(UserUtils.getUserIDTokenData()!!, friendData)
        }
    }

}


abstract class UsersViewHolder<T : UsersRecyclerUiModel>(view: View) :
    RecyclerView.ViewHolder(view) {

    abstract fun bind(model: UsersRecyclerUiModel)
}

enum class ViewType {
    SEARCH,
    ITEM,
    ITEMS_LOAD,
    TEXT
}