package com.studita.presentation.activities

import android.os.Bundle
import androidx.core.os.bundleOf
import com.studita.R
import com.studita.presentation.fragments.ToolbarFragment
import com.studita.presentation.fragments.user_statistics.UserStatFragment
import com.studita.presentation.fragments.user_statistics.UserStatOfflineModeFragment
import com.studita.presentation.fragments.user_statistics.UserStatUnLoggedInFragment
import com.studita.utils.PrefsUtils
import com.studita.utils.UserUtils
import com.studita.utils.addFragment

class UserStatActivity : DefaultActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.double_center_frame_layout)

        if (savedInstanceState == null) {
            addFragment(ToolbarFragment(), R.id.doubleTopFrameLayoutFrameLayout)
            if (UserUtils.isLoggedIn()) {
                addFragment((if(PrefsUtils.isOfflineModeEnabled()) UserStatOfflineModeFragment() else UserStatFragment()).apply {
                    arguments =
                        bundleOf("USER_ID" to this@UserStatActivity.intent.extras?.getInt("USER_ID"))
                }, R.id.doubleFrameLayoutFrameLayout)
            } else
                addFragment(UserStatUnLoggedInFragment(), R.id.doubleFrameLayoutFrameLayout)
        }

    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1)
            finish()
        else
            super.onBackPressed()
    }


}