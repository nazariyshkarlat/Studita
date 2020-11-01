package com.example.studita.presentation.fragments.user_statistics

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.entity.UserStatisticsData
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.fragments.error_fragments.InternetIsDisabledFragment
import com.example.studita.presentation.fragments.error_fragments.ServerProblemsFragment
import com.example.studita.presentation.fragments.profile.ProfileFragment
import com.example.studita.presentation.listeners.ReloadPageCallback
import com.example.studita.presentation.view_model.UserStatisticsViewModel
import com.example.studita.utils.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.user_stat_layout.*

class UserStatFragment : NavigatableFragment(R.layout.user_stat_layout), ReloadPageCallback {

    lateinit var viewModel: UserStatisticsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(UserStatisticsViewModel::class.java)

        if(savedInstanceState == null) {
            arguments?.getInt("USER_ID")?.let {
                viewModel.getUserStatistics(it)
            }
        }

        viewModel.userStatisticsState.observe(viewLifecycleOwner, Observer{
            formPages(it)
            userStatLayoutTabs.syncWithViewPager(userStatLayoutViewPager, childFragmentManager)
        })

        viewModel.progressState.observe(viewLifecycleOwner, Observer{progress->
            if(progress){
                userStatLayoutProgressBar.visibility = View.VISIBLE
                userStatLayoutScrollView.visibility = View.GONE
            }else{
                userStatLayoutProgressBar.visibility = View.GONE
                userStatLayoutScrollView.visibility = View.VISIBLE
            }
        })

        viewModel.errorEvent.observe(viewLifecycleOwner, Observer { isNetworkError->
            if (isNetworkError) {
                addFragment(InternetIsDisabledFragment(), R.id.userStatLayoutFrameLayout, false)
            }else{
                addFragment(ServerProblemsFragment(), R.id.userStatLayoutFrameLayout, false)
            }
        })

        val userId = arguments?.getInt("USER_ID")
        val isMyProfile = userId == PrefsUtils.getUserId()!!

        if (!isMyProfile) {
            val avatarLink = arguments?.getString("AVATAR_LINK")
            val userName = arguments?.getString("USER_NAME")
            userStatLayoutProfileInfo.visibility = View.VISIBLE

            userStatLayoutProfileInfo.setOnClickListener {
                (activity as AppCompatActivity).navigateTo(
                    ProfileFragment().apply {
                        arguments = bundleOf("USER_ID" to userId)
                    }, R.id.doubleFrameLayoutFrameLayout)
            }

            userStatLayoutProfileInfoUserName.text = resources.getString(
                R.string.user_name_template,
                userName
            )
            userStatLayoutProfileInfoAvatar.fillAvatar(avatarLink, userName!!, userId!!)
        }

        userStatLayoutViewPager.offscreenPageLimit = userStatLayoutTabs.getItemsSize()-1
        scrollingView = userStatLayoutScrollView
    }

    private fun formPages(userStat: List<UserStatisticsData>){
        val pageFragments = List(4) {
            UserStatPageFragment().apply {
                arguments = bundleOf(
                    "USER_ID" to this@UserStatFragment.arguments?.get("USER_ID"),
                    "USER_STAT" to Gson().toJson(userStat[it])
                )
            }
        }
        userStatLayoutTabs.setFragments(pageFragments)
        userStatLayoutTabs.setItems(
            listOf(
                resources.getString(R.string.today),
                resources.getString(R.string.yesterday),
                resources.getString(R.string.week),
                resources.getString(R.string.month)
            )
        )
    }

    override fun onPageReload() {
        arguments?.getInt("USER_ID")?.let {
            viewModel.getUserStatistics(it)
        }
    }

}