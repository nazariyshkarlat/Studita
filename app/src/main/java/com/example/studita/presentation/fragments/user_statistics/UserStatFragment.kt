package com.example.studita.presentation.fragments.user_statistics

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.interactor.UserDataStatus
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.fillAvatar
import kotlinx.android.synthetic.main.user_stat_layout.*

class UserStatFragment : NavigatableFragment(R.layout.user_stat_layout){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pageFragments= List(4){
            UserStatPageFragment().apply { arguments = bundleOf("USER_ID" to this@UserStatFragment.arguments?.get("USER_ID"), "PAGE_NUMBER" to it) }
        }

        val userId = arguments?.getInt("USER_ID")
        val isMyProfile = userId == PrefsUtils.getUserId()!!

        if(!isMyProfile){
            val avatarLink = arguments?.getString("AVATAR_LINK")
            val userName = arguments?.getString("USER_NAME")
            userStatLayoutProfileInfo.visibility = View.VISIBLE
            userStatLayoutProfileInfoUserName.text = resources.getString(
                R.string.user_name_template,
                userName)
            userStatLayoutProfileInfoAvatar.fillAvatar(avatarLink, userName!!, userId!!)
        }

        userStatLayoutTabs.setFragments(pageFragments)
        userStatLayoutTabs.setItems(listOf(resources.getString(R.string.today), resources.getString(R.string.yesterday), resources.getString(R.string.week), resources.getString(R.string.month)))

        userStatLayoutTabs.syncWithViewPager(userStatLayoutViewPager, childFragmentManager)

        scrollingView = userStatLayoutScrollView
    }

}