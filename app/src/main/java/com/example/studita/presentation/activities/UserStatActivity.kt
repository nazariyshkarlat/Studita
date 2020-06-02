package com.example.studita.presentation.activities

import android.os.Bundle
import androidx.core.os.bundleOf
import com.example.studita.R
import com.example.studita.presentation.fragments.ToolbarFragment
import com.example.studita.presentation.fragments.user_statistics.UserStatFragment
import com.example.studita.presentation.fragments.user_statistics.UserStatOfflineModeFragment
import com.example.studita.presentation.fragments.user_statistics.UserStatUnLoggedInFragment
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.UserUtils
import com.example.studita.utils.addFragment

class UserStatActivity : DefaultActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.double_center_frame_layout)

        if(savedInstanceState == null) {
            addFragment(ToolbarFragment(), R.id.doubleTopFrameLayoutFrameLayout)
            if (UserUtils.isLoggedIn()) {
                if(PrefsUtils.isOfflineMode())
                    addFragment(UserStatOfflineModeFragment(), R.id.doubleFrameLayoutFrameLayout)
                else
                    addFragment(UserStatFragment().apply {
                        arguments = bundleOf("USER_ID" to this@UserStatActivity.intent.extras?.getInt("USER_ID"))
                    }, R.id.doubleFrameLayoutFrameLayout)
            }else
                addFragment(UserStatUnLoggedInFragment(), R.id.doubleFrameLayoutFrameLayout)
        }

    }


}