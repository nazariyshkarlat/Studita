package com.studita.presentation.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.studita.R
import com.studita.presentation.fragments.base.NavigatableFragment
import com.studita.presentation.fragments.error_fragments.IncorrectTimeFragment
import com.studita.presentation.fragments.first_open.BetaTestStartFragment
import com.studita.presentation.fragments.main.MainFragment
import com.studita.presentation.view_model.MainActivityNavigationViewModel
import com.studita.utils.PrefsUtils
import com.studita.utils.TimeUtils
import com.studita.utils.addFragment

class MainActivity : DefaultActivity() {

    private val navigationViewModel: MainActivityNavigationViewModel by lazy {
        ViewModelProviders.of(this).get(MainActivityNavigationViewModel::class.java)
    }

    companion object {
        var needsRefresh = false

        fun Activity.startMainActivityClearTop(extras: Bundle? = null) {
            val intent = Intent(this, MainActivity::class.java).apply {
                extras?.let { this.putExtras(it) }
            }
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        fun getFragmentToAdd(context: Context) = when {
            !TimeUtils.timeIsAutomatically(context) -> IncorrectTimeFragment()
            !PrefsUtils.offlineDataIsCached() -> BetaTestStartFragment()
            else -> MainFragment()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame_layout)

        if (savedInstanceState == null) {
            addFragment(getFragmentToAdd(this).apply {
                arguments = intent.extras
            }, R.id.frameLayout)
        }
    }

    override fun onResume() {
        super.onResume()
        if (needsRefresh) {
            this.recreate()
            needsRefresh = false
        }
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 0){
            finish()
        }else {
            when (val currentFragment = supportFragmentManager.findFragmentById(R.id.frameLayout)) {
                is NavigatableFragment -> currentFragment.onBackClick()
                is MainFragment -> navigationViewModel.onBackPressed()
                else -> super.onBackPressed()
            }
        }
    }

}