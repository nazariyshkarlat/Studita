package com.example.studita.presentation.activities

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.utils.*
import com.example.studita.presentation.fragments.MainFragment
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.MainActivityNavigationViewModel

class MainActivity : DefaultActivity(){

    companion object{
        var needsRecreate = false
    }

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
        hideKeyboard()
        if(currentFragment is NavigatableFragment)
            currentFragment.onBackClick()
        else if(currentFragment is MainFragment)
            navigationViewModel.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        if(needsRecreate){
            needsRecreate = false
            startActivity<MainActivity>()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

}