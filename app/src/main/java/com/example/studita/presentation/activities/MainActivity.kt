package com.example.studita.presentation.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.fragments.error_fragments.IncorrectTimeFragment
import com.example.studita.presentation.fragments.first_open.OfflineModeDownloadFragment
import com.example.studita.presentation.fragments.main.MainFragment
import com.example.studita.presentation.view_model.MainActivityNavigationViewModel
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.TimeUtils
import com.example.studita.utils.addFragment
import com.example.studita.utils.startActivity

class MainActivity : DefaultActivity() {

    private lateinit var navigationViewModel: MainActivityNavigationViewModel

    companion object {
        var needsRefresh = false
        var needsRecreate = false

        fun Activity.startMainActivityNewTask(extras: Bundle? = null) {
            val intent = Intent(this, MainActivity::class.java).apply {
                extras?.let { this.putExtras(it) }
            }
            finishAffinity()
            startActivity(intent)
        }

        fun getFragmentToAdd(context: Context) = when {
            !TimeUtils.timeIsAutomatically(context) -> IncorrectTimeFragment()
            !PrefsUtils.offlineDataIsCached() -> OfflineModeDownloadFragment()
            else -> MainFragment()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame_layout)

        println(this)
        navigationViewModel =
            ViewModelProviders.of(this).get(MainActivityNavigationViewModel::class.java)

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
        } else if (needsRecreate) {
            startActivity<MainActivity>()
            needsRecreate = false
            this.finish()
        }
    }

    override fun onBackPressed() {
        when (val currentFragment = supportFragmentManager.findFragmentById(R.id.frameLayout)) {
            is NavigatableFragment -> currentFragment.onBackClick()
            is MainFragment -> navigationViewModel.onBackPressed()
            else -> super.onBackPressed()
        }
    }

}