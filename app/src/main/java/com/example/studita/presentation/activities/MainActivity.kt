package com.example.studita.presentation.activities

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.extensions.*
import com.example.studita.presentation.fragments.HomeFragment
import com.example.studita.presentation.fragments.MainFragment
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.HomeFragmentMainActivityFABViewModel
import com.example.studita.presentation.view_model.MainActivityNavigationViewModel
import kotlinx.android.synthetic.main.bottom_navigation.*
import kotlinx.android.synthetic.main.main_layout.*
import java.lang.NullPointerException
import java.lang.UnsupportedOperationException

class MainActivity : DefaultActivity(){

    lateinit var navigationViewModel : MainActivityNavigationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame_layout)

        navigationViewModel =
            ViewModelProviders.of(this).get(MainActivityNavigationViewModel::class.java)

        if(savedInstanceState == null)
            addFragment(MainFragment(), R.id.frameLayout)

    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frameLayout)
        if(currentFragment is NavigatableFragment)
            currentFragment.onBackClick()
        else if(currentFragment is MainFragment)
            navigationViewModel.onBackPressed()
    }

}