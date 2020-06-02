package com.example.studita.presentation.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.interactor.GetFriendsStatus
import com.example.studita.presentation.adapter.friends.FriendsAdapter
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.model.FriendsRecyclerUiModel
import com.example.studita.presentation.model.mapper.FriendsUiModelMapper
import com.example.studita.presentation.view_model.FriendsFragmentViewModel
import com.example.studita.utils.UserUtils
import com.example.studita.utils.dpToPx
import kotlinx.android.synthetic.main.recyclerview_layout.*

class FriendsFragment : NavigatableFragment(R.layout.recyclerview_layout){

    lateinit var friendsFragmentViewModel: FriendsFragmentViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friendsFragmentViewModel = ViewModelProviders.of(this).get(FriendsFragmentViewModel::class.java)

        recyclerViewLayoutRecyclerView.setPadding(0, resources.getDimension(R.dimen.toolbarHeight).toInt() + 10.dpToPx(), 0, 10.dpToPx())

        friendsFragmentViewModel.friendsState.observe(viewLifecycleOwner, Observer {status->
            if(status is GetFriendsStatus.Success) {
                (view as ViewGroup).removeView(recyclerViewLayoutProgressBar)
                recyclerViewLayoutRecyclerView.adapter = FriendsAdapter(ArrayList(friendsFragmentViewModel.getRecyclerItems(FriendsRecyclerUiModel.SearchUiModel,
                    status.friendsResponseData.friends.map { FriendsUiModelMapper().map(it) })), friendsFragmentViewModel)
            }
        })

        friendsFragmentViewModel.removeFragmentStatus.observe(viewLifecycleOwner, Observer {
            val friendId = it
            if(friendsFragmentViewModel.friendsState.value is GetFriendsStatus.Success){
                val friendsResponseData = (friendsFragmentViewModel.friendsState.value as GetFriendsStatus.Success).friendsResponseData
                val adapter = recyclerViewLayoutRecyclerView.adapter as FriendsAdapter
                val removeIndex = friendsResponseData.friends.indexOfFirst { it.friendId == friendId  }

                friendsResponseData.friendsCount--
                friendsResponseData.friends.removeAt(removeIndex)
                adapter.items.removeAt(removeIndex+1)

                adapter.notifyItemRemoved(removeIndex+1)
                adapter.notifyItemRangeChanged(removeIndex+1, friendsResponseData.friends.size)
            }
        })

        scrollingView = recyclerViewLayoutRecyclerView
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!isHidden){
            UserUtils.userData.userId?.let { friendsFragmentViewModel.getFriends(it) }
        }
    }

}