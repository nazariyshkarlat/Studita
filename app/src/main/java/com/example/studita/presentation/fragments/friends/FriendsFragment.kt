package com.example.studita.presentation.fragments.friends

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginTop
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studita.R
import com.example.studita.domain.interactor.users.UsersInteractor
import com.example.studita.presentation.adapter.users_list.SearchViewHolder
import com.example.studita.presentation.adapter.users_list.UsersAdapter
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.model.UsersRecyclerUiModel
import com.example.studita.presentation.model.toUserItemUiModel
import com.example.studita.presentation.view_model.FriendsFragmentViewModel
import com.example.studita.presentation.views.CustomSnackbar
import com.example.studita.utils.*
import kotlinx.android.synthetic.main.my_friends_empty.*
import kotlinx.android.synthetic.main.recyclerview_layout.*


open class FriendsFragment : NavigatableFragment(R.layout.recyclerview_layout) {

    lateinit var friendsFragmentViewModel: FriendsFragmentViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friendsFragmentViewModel =
            ViewModelProviders.of(this).get(FriendsFragmentViewModel::class.java)

        recyclerViewLayoutRecyclerView.setPadding(
            0,
            resources.getDimension(R.dimen.toolbarHeight).toInt() + 10F.dpToPx(),
            0,
            10F.dpToPx()
        )

        friendsFragmentViewModel.searchResultState.observe(viewLifecycleOwner, Observer { pair ->

            val canBeMoreItems = pair.first
            val searchResultState = pair.second

            if (recyclerViewLayoutProgressBar.marginTop == 0)
                setProgressMargin()

            if (recyclerViewLayoutRecyclerView.adapter is UsersAdapter)
                (recyclerViewLayoutRecyclerView.adapter as UsersAdapter).isEmptyView = false

            when (searchResultState) {
                is FriendsFragmentViewModel.SearchResultState.ResultsFound -> {

                    if (recyclerViewLayoutRecyclerView.adapter == null) {
                        val items =
                            if (friendsFragmentViewModel.recyclerItems != null) friendsFragmentViewModel.recyclerItems!! else ArrayList(
                                friendsFragmentViewModel.getRecyclerItems(
                                    UsersRecyclerUiModel.SearchUiModel,
                                    searchResultState.results,
                                    if (canBeMoreItems) UsersRecyclerUiModel.ProgressUiModel else null
                                )
                            )
                        val adapter = toolbarFragmentViewModel?.let { toolbarFragmentVM ->
                            UsersAdapter(
                                items,
                                view.context,
                                friendsFragmentViewModel,
                                toolbarFragmentVM,
                                arguments?.getInt("USER_ID")!!,
                                lifecycleOwner = viewLifecycleOwner
                            )
                        }
                        recyclerViewLayoutRecyclerView.adapter = adapter
                        friendsFragmentViewModel.recyclerItems = adapter?.items
                    } else {
                        val adapter = recyclerViewLayoutRecyclerView.adapter as UsersAdapter
                        val newData = listOf(
                            *searchResultState.results.map { it.toUserItemUiModel() }
                                .toTypedArray(),
                            *(if (canBeMoreItems) arrayOf(UsersRecyclerUiModel.ProgressUiModel) else emptyArray())
                        )
                        friendsFragmentViewModel.recyclerItems?.addAll(newData)

                        adapter.notifyItemRangeInserted(1, newData.size)
                    }
                }
                is FriendsFragmentViewModel.SearchResultState.MyProfileEmptyFriends -> {

                    if (arguments?.getBoolean("GLOBAL_SEARCH_ONLY") == false) {
                        recyclerViewLayoutRecyclerView.visibility = View.GONE
                        (view as ViewGroup).addView(view.makeView(R.layout.my_friends_empty))
                        myFriendsEmptyButton.setOnClickListener {

                            recyclerViewLayoutRecyclerView.visibility = View.VISIBLE
                            view.removeView(myFriendsEmptyLayout)

                            showGlobalSearchOnly(view.context)
                        }
                    } else
                        showGlobalSearchOnly(view.context)
                }
                is FriendsFragmentViewModel.SearchResultState.MoreResultsFound -> {

                    if (recyclerViewLayoutRecyclerView.adapter != null) {
                        val items = listOf(
                            *searchResultState.results.map { it.toUserItemUiModel() }
                                .toTypedArray(),
                            *(if (canBeMoreItems) arrayOf(UsersRecyclerUiModel.ProgressUiModel) else emptyArray())
                        )

                        val adapter = recyclerViewLayoutRecyclerView.adapter as UsersAdapter

                        val removePos = adapter.items.lastIndex
                        adapter.items.removeAt(removePos)
                        adapter.notifyItemRemoved(removePos)

                        val insertIndex = adapter.items.size
                        adapter.items.addAll(items)
                        adapter.notifyItemRangeInserted(
                            insertIndex,
                            items.size
                        )

                        if (!canBeMoreItems) {
                            adapter.items.removeAt(adapter.items.lastIndex)
                            adapter.notifyItemRemoved(adapter.itemCount - 1)
                        }
                    } else {
                        val adapter = toolbarFragmentViewModel?.let { toolbarFragmentVM ->
                            UsersAdapter(
                                friendsFragmentViewModel.recyclerItems!!,
                                view.context,
                                friendsFragmentViewModel,
                                toolbarFragmentVM,
                                arguments?.getInt("USER_ID")!!,
                                lifecycleOwner = viewLifecycleOwner
                            )
                        }
                        recyclerViewLayoutRecyclerView.adapter = adapter
                    }
                }
                is FriendsFragmentViewModel.SearchResultState.NoMoreResults -> {

                    if (recyclerViewLayoutRecyclerView.adapter != null) {
                        val progressIndex =
                            friendsFragmentViewModel.recyclerItems?.indexOfFirst { it is UsersRecyclerUiModel.ProgressUiModel }
                        if (progressIndex != null && progressIndex != -1) {
                            friendsFragmentViewModel.recyclerItems?.removeAt(progressIndex)
                            recyclerViewLayoutRecyclerView.adapter?.notifyItemRemoved(progressIndex)
                        }
                    } else {
                        val adapter = toolbarFragmentViewModel?.let { toolbarFragmentVM ->
                            UsersAdapter(
                                friendsFragmentViewModel.recyclerItems!!,
                                view.context,
                                friendsFragmentViewModel,
                                toolbarFragmentVM,
                                arguments?.getInt("USER_ID")!!,
                                lifecycleOwner = viewLifecycleOwner
                            )
                        }
                        recyclerViewLayoutRecyclerView.adapter = adapter
                    }
                }
                is FriendsFragmentViewModel.SearchResultState.GlobalSearchNotFound, FriendsFragmentViewModel.SearchResultState.SearchFriendsNotFound, FriendsFragmentViewModel.SearchResultState.GlobalSearchEnterText -> {

                    if (recyclerViewLayoutRecyclerView.adapter == null) {
                        val adapter = toolbarFragmentViewModel?.let { toolbarFragmentVM ->
                            UsersAdapter(
                                friendsFragmentViewModel.recyclerItems!!,
                                view.context,
                                friendsFragmentViewModel,
                                toolbarFragmentVM,
                                arguments?.getInt("USER_ID")!!,
                                lifecycleOwner = viewLifecycleOwner
                            )
                        }
                        recyclerViewLayoutRecyclerView.adapter = adapter
                    }

                    recyclerViewLayoutRecyclerView?.adapter?.let { adapter ->

                        val removeCount = adapter.itemCount - 2

                        (recyclerViewLayoutRecyclerView.adapter as UsersAdapter).isEmptyView = true

                        adapter.notifyItemRangeRemoved(2, removeCount)

                        adapter.notifyItemChanged(1, Unit)
                    }
                }
            }
        })

        friendsFragmentViewModel.progressState.observe(viewLifecycleOwner, Observer { progress ->
            if (progress) {
                recyclerViewLayoutRecyclerView?.adapter?.let {
                    val removeCount = it.itemCount - 1
                    friendsFragmentViewModel.recyclerItems!!.removeAll { it1 -> it1 !is UsersRecyclerUiModel.SearchUiModel }
                    (it as UsersAdapter).isEmptyView = false
                    it.notifyItemRangeRemoved(1, removeCount)
                }
                recyclerViewLayoutProgressBar.visibility = View.VISIBLE
            } else {
                recyclerViewLayoutProgressBar.visibility = View.GONE
            }
        })

        friendsFragmentViewModel.addFriendStatus.observe(viewLifecycleOwner, Observer {

            val snackbar = CustomSnackbar(view.context)
            snackbar.show(
                resources.getString(
                    when (it) {
                        is UsersInteractor.FriendActionState.FriendshipRequestIsAccepted -> R.string.friend_added_snackbar
                        is UsersInteractor.FriendActionState.RemovedFromFriends -> R.string.friend_removed_snackbar
                        is UsersInteractor.FriendActionState.FriendshipRequestIsCanceled -> R.string.friendship_request_is_canceled_snackbar
                        is UsersInteractor.FriendActionState.FriendshipRequestIsSent -> R.string.friendship_request_is_sent_snackbar
                        is UsersInteractor.FriendActionState.FriendshipRequestIsRejected -> R.string.friendship_request_is_rejected_snackbar
                    },
                    it.userData.userName
                ),
                ThemeUtils.getAccentColor(snackbar.context),
                duration = resources.getInteger(R.integer.add_remove_friend_snackbar_duration)
                    .toLong()
            )
        })

        UserUtils.isMyFriendLiveData.observe(viewLifecycleOwner, Observer { friendActionState ->

            val itemIndex =
                friendsFragmentViewModel.recyclerItems?.indexOfFirst { (it is UsersRecyclerUiModel.UserItemUiModel) && it.userId == friendActionState.userData.userId }

            if (itemIndex != null && itemIndex != -1) {
                friendsFragmentViewModel.userData?.find { it.userId == friendActionState.userData.userId }?.isMyFriendStatus =
                    friendActionState.userData.isMyFriendStatus
                (friendsFragmentViewModel.recyclerItems?.get(itemIndex) as UsersRecyclerUiModel.UserItemUiModel).isMyFriendStatus =
                    friendActionState.userData.isMyFriendStatus
                recyclerViewLayoutRecyclerView.adapter?.notifyItemChanged(itemIndex, Unit)
            }
        })


        if (savedInstanceState == null) {
            arguments?.getInt("USER_ID")?.let {
                friendsFragmentViewModel.getUsers(
                    it,
                    friendsFragmentViewModel.sortBy,
                    false,
                    null,
                    false
                )
            }
        }

        recyclerViewLayoutRecyclerView.layoutManager = object : LinearLayoutManager(view.context) {
            override fun requestChildRectangleOnScreen(
                parent: RecyclerView,
                child: View,
                rect: Rect,
                immediate: Boolean
            ): Boolean {
                return false
            }

            override fun requestChildRectangleOnScreen(
                parent: RecyclerView,
                child: View,
                rect: Rect,
                immediate: Boolean,
                focusedChildVisible: Boolean
            ): Boolean {
                return false
            }
        }

        scrollingView = recyclerViewLayoutRecyclerView
    }

    override fun onBackClick() {

        val lastState = friendsFragmentViewModel.searchState

        if (friendsFragmentViewModel.searchState != FriendsFragmentViewModel.SearchState.NoSearch && !friendsFragmentViewModel.globalSearchOnly) {
            recyclerViewLayoutRecyclerView.scrollToPosition(0)
            friendsFragmentViewModel.searchState = FriendsFragmentViewModel.SearchState.NoSearch
            (recyclerViewLayoutRecyclerView.adapter as UsersAdapter).onSearchVisibilityChanged(false)
            recyclerViewLayoutRecyclerView.post {
                if (recyclerViewLayoutRecyclerView.findViewHolderForAdapterPosition(0) is SearchViewHolder) {
                    (recyclerViewLayoutRecyclerView.findViewHolderForAdapterPosition(0) as SearchViewHolder).searchState =
                        FriendsFragmentViewModel.SearchState.NoSearch
                    recyclerViewLayoutRecyclerView.adapter?.notifyItemChanged(0, Unit)
                }
                arguments?.getInt("USER_ID")?.let {
                    if (!((lastState is FriendsFragmentViewModel.SearchState.FriendsSearch) &&
                                lastState.startsWith!!.isEmpty())
                    ) {
                        friendsFragmentViewModel.getUsers(
                            it,
                            friendsFragmentViewModel.sortBy,
                            false,
                            null,
                            false
                        )
                    }
                }
            }
            (activity as AppCompatActivity).hideKeyboard()
        } else {
            super.onBackClick()
        }
    }

    private fun setProgressMargin() {
        val param = recyclerViewLayoutProgressBar.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(0, 64F.dpToPx(), 0, 0)
        recyclerViewLayoutProgressBar.layoutParams = param
    }

    private fun showGlobalSearchOnly(context: Context) {
        friendsFragmentViewModel.globalSearchOnly = true
        val adapter = toolbarFragmentViewModel?.let { toolbarFragmentVM ->
            toolbarFragmentVM.setToolbarText(context.resources.getString(R.string.search))
            UsersAdapter(
                arrayListOf(UsersRecyclerUiModel.SearchUiModel),
                context,
                friendsFragmentViewModel,
                toolbarFragmentVM,
                arguments?.getInt("USER_ID")!!,
                lifecycleOwner = viewLifecycleOwner
            )
        }
        recyclerViewLayoutRecyclerView.adapter = adapter
        friendsFragmentViewModel.recyclerItems = adapter?.items
        friendsFragmentViewModel.searchState = FriendsFragmentViewModel.SearchState.GlobalSearch("")
        friendsFragmentViewModel.searchResultState.value =
            (friendsFragmentViewModel.searchResultState.value?.first == true) to FriendsFragmentViewModel.SearchResultState.GlobalSearchEnterText
    }

}