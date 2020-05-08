package com.example.studita.presentation.activities

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.interesting.InterestingLoadFragment
import com.example.studita.utils.navigateTo
import com.example.studita.presentation.view_model.InterestingViewModel

class InterestingActivity : DefaultActivity() {

    private var interestingViewModel : InterestingViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame_layout)
        interestingViewModel = ViewModelProviders.of(this).get(InterestingViewModel::class.java)

        if(savedInstanceState == null) {
            val extras = intent.extras
            if(extras != null){
                interestingViewModel?.getInteresting(1)
            }
            navigateTo(InterestingLoadFragment(), R.id.frameLayout)
        }
    }

    override fun onBackPressed() {
        finish()
    }

}