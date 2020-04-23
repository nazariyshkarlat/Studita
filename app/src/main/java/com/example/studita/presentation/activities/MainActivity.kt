package com.example.studita.presentation.activities

import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.example.studita.R
import com.example.studita.presentation.fragments.HomeFragment
import com.example.studita.presentation.fragments.IncorrectTimeFragment
import com.example.studita.presentation.fragments.MainFragment
import com.example.studita.presentation.utils.*
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.MainActivityNavigationViewModel
import com.example.studita.presentation.view_model.MainFragmentViewModel
import kotlinx.android.synthetic.main.bottom_navigation.*
import kotlinx.android.synthetic.main.frame_layout.*
import kotlinx.android.synthetic.main.main_layout.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.UnsupportedOperationException
import java.sql.Time

class MainActivity : DefaultActivity(){

    private lateinit var navigationViewModel : MainActivityNavigationViewModel

    companion object{
        var needsRecreate = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame_layout)

        navigationViewModel =
            ViewModelProviders.of(this).get(MainActivityNavigationViewModel::class.java)

        if(savedInstanceState == null) {
            if(TimeUtils.timeIsAutomatically(frameLayout.context))
                addFragment(MainFragment(), R.id.frameLayout)
            else
                addFragment(IncorrectTimeFragment(), R.id.frameLayout)
        }

    }

    override fun onResume() {
        super.onResume()
        if (needsRecreate) {
            needsRecreate = false
            startActivity<MainActivity>()
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