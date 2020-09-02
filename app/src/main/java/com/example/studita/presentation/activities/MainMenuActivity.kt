package com.example.studita.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.main.MainMenuFragment
import com.example.studita.presentation.fragments.notifications.NotificationsFragment
import com.example.studita.presentation.fragments.profile.ProfileMenuFragment
import com.example.studita.presentation.fragments.ToolbarFragment
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.MainMenuActivityViewModel
import com.example.studita.utils.UserUtils
import com.example.studita.utils.addFragment
import com.example.studita.utils.navigateTo


class MainMenuActivity : DefaultActivity() {

    companion object {
        const val NOTIFICATIONS_REQUEST_CODE = 7534
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.double_center_frame_layout)

        val viewModel = ViewModelProviders.of(this).get(MainMenuActivityViewModel::class.java)

        viewModel.onThemeChangeListener = this
        if (savedInstanceState == null) {
            addFragment(ToolbarFragment(), R.id.doubleTopFrameLayoutFrameLayout)

            navigateTo(
                if (UserUtils.isLoggedIn()) ProfileMenuFragment() else MainMenuFragment(),
                R.id.doubleFrameLayoutFrameLayout
            )
            if (intent.getIntExtra("REQUEST_CODE", 0) == NOTIFICATIONS_REQUEST_CODE) {
                supportFragmentManager.executePendingTransactions()
                navigateTo(NotificationsFragment(), R.id.doubleFrameLayoutFrameLayout)
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            if (isTaskRoot) {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            } else
                finish()
        } else {
            val f =
                supportFragmentManager.findFragmentById(R.id.doubleFrameLayoutFrameLayout) as NavigatableFragment
            f.onBackClick()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.getIntExtra("REQUEST_CODE", 0) == NOTIFICATIONS_REQUEST_CODE) {
            if (supportFragmentManager.findFragmentById(R.id.doubleFrameLayoutFrameLayout) !is NotificationsFragment)
                navigateTo(NotificationsFragment(), R.id.doubleFrameLayoutFrameLayout)
        }
    }

}