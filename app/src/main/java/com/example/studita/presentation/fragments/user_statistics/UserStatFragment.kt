package com.example.studita.presentation.fragments.user_statistics

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.example.studita.R
import com.example.studita.presentation.fragments.base.NavigatableFragment
import kotlinx.android.synthetic.main.user_stat_layout.*

class UserStatFragment : NavigatableFragment(R.layout.user_stat_layout){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pageFragments= List(4){
            UserStatPageFragment().apply { arguments = bundleOf("USER_ID" to this@UserStatFragment.arguments?.get("USER_ID"), "PAGE_NUMBER" to it) }}

        userStatLayoutTabs.setFragments(pageFragments)
        userStatLayoutTabs.setItems(listOf(resources.getString(R.string.today), resources.getString(R.string.yesterday), resources.getString(R.string.week), resources.getString(R.string.month)))

        userStatLayoutTabs.syncWithViewPager(userStatLayoutViewPager, childFragmentManager)

        scrollingView = userStatLayoutScrollView
    }

}