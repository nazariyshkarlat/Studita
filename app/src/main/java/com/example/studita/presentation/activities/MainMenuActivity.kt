package com.example.studita.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.utils.addFragment
import com.example.studita.utils.navigateTo
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.fragments.MainMenuFragment
import com.example.studita.presentation.fragments.NotificationsFragment
import com.example.studita.presentation.fragments.ProfileMenuFragment
import com.example.studita.presentation.fragments.ToolbarFragment
import com.example.studita.presentation.view_model.MainMenuActivityViewModel
import com.example.studita.utils.UserUtils

class MainMenuActivity : DefaultActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.double_center_frame_layout)

        val viewModel = ViewModelProviders.of(this).get(MainMenuActivityViewModel::class.java)

        viewModel.onThemeChangeListener = this
        if(savedInstanceState == null) {
            addFragment(ToolbarFragment(), R.id.doubleTopFrameLayoutFrameLayout)

            if(intent.getBooleanExtra("OPEN_NOTIFICATIONS", false))
                navigateTo(NotificationsFragment(), R.id.doubleFrameLayoutFrameLayout)
            else
                navigateTo(if(UserUtils.isLoggedIn()) ProfileMenuFragment() else MainMenuFragment(), R.id.doubleFrameLayoutFrameLayout)
        }
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 1)
            this.finish()
        else{
            val f = supportFragmentManager.findFragmentById(R.id.doubleFrameLayoutFrameLayout) as NavigatableFragment
            f.onBackClick()
        }
    }

}