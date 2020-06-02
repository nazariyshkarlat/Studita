package com.example.studita.presentation.activities

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.IncorrectTimeFragment
import com.example.studita.presentation.fragments.MainFragment
import com.example.studita.utils.*
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.MainActivityNavigationViewModel
import kotlinx.android.synthetic.main.frame_layout.*
import java.sql.Time

class MainActivity : DefaultActivity(){

    private lateinit var navigationViewModel : MainActivityNavigationViewModel

    companion object{
        var needsRefresh = false
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
        if (needsRefresh) {
            needsRefresh = false
            this.recreate()
        }else if(needsRecreate){
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