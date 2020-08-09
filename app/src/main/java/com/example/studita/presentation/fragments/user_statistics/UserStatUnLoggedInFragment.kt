package com.example.studita.presentation.fragments.user_statistics

import android.os.Bundle
import android.view.View
import com.example.studita.R
import com.example.studita.presentation.activities.MainMenuActivity
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.utils.startActivity
import kotlinx.android.synthetic.main.user_stat_un_logged_layout.*

class UserStatUnLoggedInFragment : NavigatableFragment(R.layout.user_stat_un_logged_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userStatUnLoggedLayoutButton.setOnClickListener {
            activity?.startActivity<MainMenuActivity>()
        }
    }

}