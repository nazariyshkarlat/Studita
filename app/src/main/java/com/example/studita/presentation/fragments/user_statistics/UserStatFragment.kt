package com.example.studita.presentation.fragments.user_statistics

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.interactor.UserDataStatus
import com.example.studita.domain.interactor.UserStatisticsStatus
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.UserStatisticsViewModel
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.UserUtils
import com.example.studita.utils.fillAvatar
import kotlinx.android.synthetic.main.user_stat_layout.*

class UserStatFragment : NavigatableFragment(R.layout.user_stat_layout){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProviders.of(this).get(UserStatisticsViewModel::class.java)

        val pageFragments= List(4){
            UserStatPageFragment().apply { arguments = bundleOf("USER_ID" to this@UserStatFragment.arguments?.get("USER_ID"), "PAGE_NUMBER" to it) }
        }

        val isMyProfile = arguments?.getInt("USER_ID") == PrefsUtils.getUserId()!!

        if(!isMyProfile){
            if(savedInstanceState == null){
                viewModel.getUserData(arguments?.getInt("USER_ID")!!)
            }
            userStatLayoutProfileInfo.visibility = View.VISIBLE

            viewModel.userDataState.observe(viewLifecycleOwner, Observer {
                if(it is UserDataStatus.Success) {
                    userStatLayoutProfileInfoUserName.text = resources.getString(
                        R.string.user_name_template,
                        it.result.userName
                    )
                    userStatLayoutProfileInfoAvatar.fillAvatar(it.result.avatarLink, it.result.userName!!, it.result.userId!!)
                }
            })
        }

        userStatLayoutTabs.setFragments(pageFragments)
        userStatLayoutTabs.setItems(listOf(resources.getString(R.string.today), resources.getString(R.string.yesterday), resources.getString(R.string.week), resources.getString(R.string.month)))

        userStatLayoutTabs.syncWithViewPager(userStatLayoutViewPager, childFragmentManager)

        scrollingView = userStatLayoutScrollView
    }

}