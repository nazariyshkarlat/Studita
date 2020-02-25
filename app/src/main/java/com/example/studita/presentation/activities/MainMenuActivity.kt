package com.example.studita.presentation.activities

import android.os.Bundle
import com.example.studita.R
import com.example.studita.presentation.extensions.addFragment
import com.example.studita.presentation.extensions.navigateTo
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.fragments.MainMenuFragment
import com.example.studita.presentation.fragments.ToolbarFragment

class MainMenuActivity : DefaultActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.double_constraint_frame_layout)

        if(savedInstanceState == null) {
            addFragment(ToolbarFragment(), R.id.doubleTopConstraintFrameLayout)
            navigateTo(MainMenuFragment(), R.id.doubleConstraintFrameLayout)
        }
    }


    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 1)
            this.finish()
        else{
            val f = supportFragmentManager.findFragmentById(R.id.doubleConstraintFrameLayout) as NavigatableFragment
            f.onBackClick()
        }
    }

}