package com.example.studita.presentation.activities

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.extensions.*
import com.example.studita.presentation.fragments.MainFragment
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.MainActivityNavigationViewModel

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