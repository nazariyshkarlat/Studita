package com.example.studita.presentation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.extensions.getAppCompatActivity
import com.example.studita.presentation.extensions.navigateTo
import com.example.studita.presentation.fragments.ExercisesCloseDialogAlertFragment
import com.example.studita.presentation.fragments.ExercisesFragment
import com.example.studita.presentation.fragments.ExercisesLoadFragment
import com.example.studita.presentation.fragments.ExercisesResultFragment
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.ExercisesViewModel
import com.example.studita.presentation.view_model.MainActivityNavigationViewModel

class ExercisesActivity : DefaultActivity() {

    private var exercisesViewModel : ExercisesViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame_layout)

        exercisesViewModel = ViewModelProviders.of(this).get(ExercisesViewModel::class.java)

        val extras = intent.extras
        if(extras != null){
            val chapterPartNumber = extras.getInt("CHAPTER_PART_NUMBER")
            if(chapterPartNumber != 0){
                exercisesViewModel?.getExercises(chapterPartNumber)
            }
        }

        if(savedInstanceState == null)
            navigateTo(ExercisesLoadFragment(), R.id.frameLayout)
    }

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

}