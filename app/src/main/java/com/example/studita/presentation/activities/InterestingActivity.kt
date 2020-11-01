package com.example.studita.presentation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.interesting.InterestingExplanationFragment
import com.example.studita.presentation.fragments.interesting.InterestingFragment
import com.example.studita.presentation.fragments.interesting.InterestingLoadFragment
import com.example.studita.presentation.fragments.interesting.InterestingStartScreenFragment
import com.example.studita.presentation.view_model.InterestingViewModel
import com.example.studita.utils.addFragment
import com.example.studita.utils.navigateTo

class InterestingActivity : DefaultActivity() {

    private var interestingViewModel: InterestingViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame_layout)
        interestingViewModel = ViewModelProviders.of(this).get(InterestingViewModel::class.java)

        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras != null) {
                interestingViewModel?.interestingNumber = extras.getInt("INTERESTING_NUMBER")
                interestingViewModel?.getInteresting()
            }
            addFragment(InterestingLoadFragment(), R.id.frameLayout, addToBackStack = false)
        }
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frameLayout)
        if(currentFragment is InterestingFragment)
            currentFragment.onBackClick()
        else
            super.onBackPressed()
    }

}