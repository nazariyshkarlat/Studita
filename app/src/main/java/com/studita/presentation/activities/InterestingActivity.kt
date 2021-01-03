package com.studita.presentation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.studita.R
import com.studita.presentation.fragments.interesting.InterestingExplanationFragment
import com.studita.presentation.fragments.interesting.InterestingFragment
import com.studita.presentation.fragments.interesting.InterestingLoadFragment
import com.studita.presentation.fragments.interesting.InterestingStartScreenFragment
import com.studita.presentation.view_model.ExercisesViewModel
import com.studita.presentation.view_model.InterestingViewModel
import com.studita.utils.addFragment
import com.studita.utils.navigateTo

@Suppress("UNCHECKED_CAST")
class InterestingActivity : DefaultActivity() {

    private var interestingViewModel: InterestingViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame_layout)
        interestingViewModel = ViewModelProviders.of(this, object : AbstractSavedStateViewModelFactory(this, null) {
            override fun <T : ViewModel?> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                return InterestingViewModel(intent!!.extras!!.getInt("INTERESTING_NUMBER"), handle) as T
            }
        })[InterestingViewModel::class.java]

        if (savedInstanceState == null) {
            addFragment(InterestingLoadFragment(), R.id.frameLayout, addToBackStack = false)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        interestingViewModel?.saveState()
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frameLayout)
        if(currentFragment is InterestingFragment)
            currentFragment.onBackClick()
        else
            super.onBackPressed()
    }

}