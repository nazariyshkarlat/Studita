package com.example.studita.presentation.activities

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.exercises.ExercisesLoadFragment
import com.example.studita.presentation.utils.navigateTo
import com.example.studita.presentation.fragments.exercises.ExercisesFragment
import com.example.studita.presentation.fragments.exercises.ExercisesResultFragment
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.ExercisesViewModel

class ExercisesActivity : DefaultActivity() {

    private var exercisesViewModel : ExercisesViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame_layout)

        exercisesViewModel = ViewModelProviders.of(this).get(ExercisesViewModel::class.java)

        if(savedInstanceState == null) {
            val extras = intent.extras
            if(extras != null){
                val chapterPartNumber = extras.getInt("CHAPTER_PART_NUMBER")
                val chapterPartsCount = extras.getInt("CHAPTER_PARTS_COUNT")
                val chapterNumber = extras.getInt("CHAPTER_NUMBER")
                exercisesViewModel?.chapterPartsCount = chapterPartsCount
                exercisesViewModel?.chapterPartNumber = chapterPartNumber
                exercisesViewModel?.chapterNumber = chapterNumber
                exercisesViewModel?.isTraining = extras.getBoolean("IS_TRAINING")
                exercisesViewModel?.getExercises(chapterPartNumber)
            }
            navigateTo(ExercisesLoadFragment(), R.id.frameLayout)
        }
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