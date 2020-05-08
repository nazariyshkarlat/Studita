package com.example.studita.presentation.fragments.user_statistics

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.os.bundleOf
import com.example.studita.R
import com.example.studita.presentation.fragments.base.BaseFragment
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.android.synthetic.main.user_stat_layout.*

class UserStatFragment : BaseFragment(R.layout.user_stat_layout), ViewTreeObserver.OnScrollChangedListener{

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pageFragments= List(4){
            UserStatPageFragment()
                .apply { arguments = bundleOf("PAGE_NUMBER" to it) }}

        userStatLayoutTabs.setFragments(pageFragments)
        userStatLayoutTabs.setItems(listOf(resources.getString(R.string.today), resources.getString(R.string.yesterday), resources.getString(R.string.week), resources.getString(R.string.month)))

        fragmentManager?.let { userStatLayoutTabs.syncWithViewPager(userStatLayoutViewPager, it) }

        userStatLayoutScrollView.viewTreeObserver
            .addOnScrollChangedListener(this)

        toolbarLayoutTitle.text = resources.getString(R.string.stat)
        toolbarLayoutBackButton.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        userStatLayoutScrollView.viewTreeObserver
            .removeOnScrollChangedListener(this)
        super.onDestroy()

    }

    override fun onScrollChanged() {
        val scrollY: Int = userStatLayoutScrollView.scrollY
        toolbarLayout.background = if (scrollY != 0) context?.getDrawable(R.drawable.divider_bottom_drawable) else null
    }

}