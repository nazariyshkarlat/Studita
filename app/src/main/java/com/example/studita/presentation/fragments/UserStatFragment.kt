package com.example.studita.presentation.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import com.example.studita.R
import com.example.studita.presentation.extensions.shareImg
import com.example.studita.presentation.fragments.base.NavigatableFragment
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.android.synthetic.main.user_stat_layout.*


class UserStatFragment : NavigatableFragment(R.layout.user_stat_layout), ViewTreeObserver.OnScrollChangedListener{

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userStatLayoutTabs.setItems(listOf("Сегодня", "Вчера", "Неделя", "Месяц"))

        fragmentManager?.let { userStatLayoutTabs.syncWithViewPager(userStatLayoutViewPager, it) }

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
        if (scrollY != 0)
            toolbarLayout.background = context?.getDrawable(R.drawable.divider_bottom_drawable)
        else
            toolbarLayout.background = null
    }

}