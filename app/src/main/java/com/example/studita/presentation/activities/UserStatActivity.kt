package com.example.studita.presentation.activities

import android.os.Bundle
import com.example.studita.R
import com.example.studita.presentation.fragments.user_statistics.UserStatFragment
import com.example.studita.presentation.fragments.user_statistics.UserStatUnLoggedInFragment
import com.example.studita.presentation.utils.UserUtils
import com.example.studita.presentation.utils.addFragment
import kotlinx.android.synthetic.main.toolbar_layout.*

class UserStatActivity : DefaultActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame_layout)

        if(savedInstanceState == null) {
            if (UserUtils.isLoggedIn())
                addFragment(UserStatFragment(), R.id.frameLayout)
            else
                addFragment(UserStatUnLoggedInFragment(), R.id.frameLayout)
        }

    }


}