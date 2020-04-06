package com.example.studita.presentation.fragments.user_statistics

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import com.example.studita.R
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.fragments.user_statistics.pages.UserStatMonthFragment
import com.example.studita.presentation.fragments.user_statistics.pages.UserStatTodayFragment
import com.example.studita.presentation.fragments.user_statistics.pages.UserStatWeekFragment
import com.example.studita.presentation.fragments.user_statistics.pages.UserStatYesterdayFragment
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.android.synthetic.main.user_stat_layout.*


class UserStatFragment : NavigatableFragment(R.layout.user_stat_layout), ViewTreeObserver.OnScrollChangedListener{

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userStatLayoutTabs.setFragments(listOf(UserStatTodayFragment(), UserStatYesterdayFragment(), UserStatWeekFragment(), UserStatMonthFragment()))
        userStatLayoutTabs.setItems(listOf(resources.getString(R.string.today), resources.getString(R.string.yesterday), resources.getString(R.string.week), resources.getString(R.string.month)))

        userStatLayoutTabs.syncWithViewPager(userStatLayoutViewPager, childFragmentManager)

        userStatLayoutScrollView.viewTreeObserver
            .addOnScrollChangedListener(this)

        toolbarLayoutTitle.text = resources.getString(R.string.stat)
        toolbarLayoutBackButton.setOnClickListener {
            activity?.onBackPressed()
        }

        userStatLayoutShareButton.setOnClickListener {
        }

    }

    override fun onDestroyView() {
        userStatLayoutScrollView.viewTreeObserver
            .removeOnScrollChangedListener(this)
        super.onDestroyView()
    }

    override fun onScrollChanged() {
        val scrollY: Int = userStatLayoutScrollView.scrollY
        toolbarLayout.background = if (scrollY != 0) context?.getDrawable(R.drawable.divider_bottom_drawable) else null
    }

}