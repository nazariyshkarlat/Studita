package com.example.studita.presentation.activities

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.utils.addFragment
import com.example.studita.utils.navigateTo
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.fragments.MainMenuFragment
import com.example.studita.presentation.fragments.ToolbarFragment
import com.example.studita.presentation.view_model.MainMenuActivityViewModel

class MainMenuActivity : DefaultActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.double_center_frame_layout)

        val viewModel = ViewModelProviders.of(this).get(MainMenuActivityViewModel::class.java)

        if(savedInstanceState == null) {
            addFragment(ToolbarFragment(), R.id.doubleTopFrameLayoutFrameLayout)
            val mainMenuFragment =
                MainMenuFragment()
            viewModel.onThemeChangeListener = this
            navigateTo(mainMenuFragment, R.id.doubleFrameLayoutFrameLayout)
        }
    }


    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 1)
            this.finish()
        else{
            val f = supportFragmentManager.findFragmentById(R.id.doubleFrameLayoutFrameLayout) as NavigatableFragment
            f.onBackClick()
        }
    }

}