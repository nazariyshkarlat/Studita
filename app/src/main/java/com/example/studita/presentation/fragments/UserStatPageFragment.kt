package com.example.studita.presentation.fragments

import android.os.Bundle
import android.view.View
import com.example.studita.R
import com.example.studita.presentation.extensions.styleTimeText
import com.example.studita.presentation.fragments.base.BaseFragment
import kotlinx.android.synthetic.main.user_stat_page_layout.*

class UserStatPageFragment : BaseFragment(R.layout.user_stat_page_layout){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userStatPageLayoutTimeSubtitle.text = context?.let { styleTimeText(it, userStatPageLayoutTimeSubtitle.text.toString()) }
    }

}