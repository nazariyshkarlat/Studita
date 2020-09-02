package com.example.studita.presentation.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.IncorrectTimeFragment
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.fragments.main.MainFragment
import com.example.studita.presentation.view_model.MainActivityNavigationViewModel
import com.example.studita.utils.TimeUtils
import com.example.studita.utils.addFragment
import com.example.studita.utils.startActivity
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.frame_layout.*

class MainActivity : DefaultActivity() {

    private lateinit var navigationViewModel: MainActivityNavigationViewModel

    companion object {
        var needsRefresh = false
        var needsRecreate = false

        fun Activity.startMainActivityNewTask() {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame_layout)
        
        navigationViewModel =
            ViewModelProviders.of(this).get(MainActivityNavigationViewModel::class.java)

        if (savedInstanceState == null) {
            if (TimeUtils.timeIsAutomatically(frameLayout.context))
                addFragment(MainFragment(), R.id.frameLayout)
            else
                addFragment(IncorrectTimeFragment(), R.id.frameLayout)
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