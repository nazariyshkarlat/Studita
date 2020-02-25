package com.example.studita.presentation.activities

import android.os.Bundle
import com.example.studita.R
import com.example.studita.presentation.extensions.navigateTo
import com.example.studita.presentation.fragments.ExercisesCloseDialogAlertFragment
import com.example.studita.presentation.fragments.ExercisesFragment
import com.example.studita.presentation.fragments.ExercisesLoadFragment
import com.example.studita.presentation.fragments.ExercisesResultFragment
import com.example.studita.presentation.fragments.base.NavigatableFragment

class ExercisesActivity : DefaultActivity() {

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        val childFragment =  supportFragmentManager.findFragmentById(R.id.frameLayout)
        if(childFragment is ExercisesFragment)
            childFragment.onWindowFocusChanged(hasFocus)
    }

    override fun onBackPressed() {
        val childFragment =  supportFragmentManager.findFragmentById(R.id.exercisesEndLayoutFrameLayout)
        if(childFragment == null) {
            when (val fragment = supportFragmentManager.findFragmentById(R.id.frameLayout)) {
                is ExercisesFragment -> {
                    fragment.onBackClick()
                }
                is NavigatableFragment -> fragment.onBackClick()
                else -> this.finish()
            }
        }else{
            if(childFragment is ExercisesResultFragment)
                this.finish()
            else if(childFragment is NavigatableFragment)
                childFragment.onBackClick()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame_layout)

        if(savedInstanceState == null)
            navigateTo(ExercisesLoadFragment(), R.id.frameLayout)
    }

}