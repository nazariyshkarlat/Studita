package com.example.studita.presentation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.CompetitionsFragment
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.fragments.exercises.ExercisesFragment
import com.example.studita.presentation.fragments.exercises.ExercisesResultFragment
import com.example.studita.presentation.fragments.interesting.InterestingLoadFragment
import com.example.studita.presentation.utils.navigateTo
import com.example.studita.presentation.utils.replaceWithAnim
import com.example.studita.presentation.view_model.ExercisesViewModel
import com.example.studita.presentation.view_model.InterestingViewModel
import kotlinx.android.synthetic.main.exercise_layout.*
import kotlinx.android.synthetic.main.frame_layout.*

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