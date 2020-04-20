package com.example.studita.presentation.activities

import android.os.Bundle
import android.view.ViewTreeObserver
import com.example.studita.R
import com.example.studita.presentation.fragments.user_statistics.pages.UserStatMonthFragment
import com.example.studita.presentation.fragments.user_statistics.pages.UserStatTodayFragment
import com.example.studita.presentation.fragments.user_statistics.pages.UserStatWeekFragment
import com.example.studita.presentation.fragments.user_statistics.pages.UserStatYesterdayFragment
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.android.synthetic.main.user_stat_layout.*

class UserStatActivity : DefaultActivity(), ViewTreeObserver.OnScrollChangedListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_stat_layout)

        userStatLayoutTabs.setFragments(listOf(UserStatTodayFragment(), UserStatYesterdayFragment(), UserStatWeekFragment(), UserStatMonthFragment()))
        userStatLayoutTabs.setItems(listOf(resources.getString(R.string.today), resources.getString(R.string.yesterday), resources.getString(R.string.week), resources.getString(R.string.month)))

        userStatLayoutViewPager.offscreenPageLimit = 3
        userStatLayoutTabs.syncWithViewPager(userStatLayoutViewPager, supportFragmentManager)

        userStatLayoutScrollView.viewTreeObserver
            .addOnScrollChangedListener(this)

        toolbarLayoutTitle.text = resources.getString(R.string.stat)
        toolbarLayoutBackButton.setOnClickListener {
            onBackPressed()
        }
    }


    override fun onDestroy() {
        userStatLayoutScrollView.viewTreeObserver
            .removeOnScrollChangedListener(this)
        super.onDestroy()

    }

    override fun onScrollChanged() {
        val scrollY: Int = userStatLayoutScrollView.scrollY
        toolbarLayout.background = if (scrollY != 0) getDrawable(R.drawable.divider_bottom_drawable) else null
    }

}