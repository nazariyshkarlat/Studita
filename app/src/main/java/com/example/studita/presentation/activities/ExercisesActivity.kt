package com.example.studita.presentation.activities

import android.os.Bundle
import android.view.MotionEvent
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.fragments.dialog_alerts.ExercisesCloseDialogAlertFragment
import com.example.studita.presentation.fragments.exercises.ExercisesLoadFragment
import com.example.studita.presentation.fragments.exercises.ExercisesResultFragment
import com.example.studita.presentation.view_model.ExercisesViewModel
import com.example.studita.utils.addFragment
import com.example.studita.utils.navigateTo

class ExercisesActivity : DefaultActivity() {

    private var exercisesViewModel: ExercisesViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame_layout)

        exercisesViewModel = ViewModelProviders.of(this).get(ExercisesViewModel::class.java)

        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras != null) {
                exercisesViewModel?.let {
                    it.chapterPartsInChapterCount = extras.getInt("CHAPTER_PARTS_IN_CHAPTER_COUNT")
                    it.chapterPartNumber = extras.getInt("CHAPTER_PART_NUMBER")
                    it.chapterNumber = extras.getInt("CHAPTER_NUMBER")
                    it.exercisesInChapterCount = extras.getInt("EXERCISES_IN_CHAPTER_COUNT")
                    it.chapterName = extras.getString("CHAPTER_NAME")
                    it.chapterPartInChapterNumber = extras.getInt("CHAPTER_PART_IN_CHAPTER_NUMBER")
                    it.isTraining = extras.getBoolean("IS_TRAINING")
                    it.getExercises(it.chapterPartNumber)
                }
            }
            addFragment(ExercisesLoadFragment(), R.id.frameLayout)
        }
    }

    override fun onBackPressed() {
        val childFragment =
            supportFragmentManager.findFragmentById(R.id.exercisesEndLayoutFrameLayout)
        if (childFragment == null) {
            if (exercisesViewModel?.exercisesResultSentToServer() == false && supportFragmentManager.findFragmentById(R.id.frameLayout) !is ExercisesLoadFragment)
                onBackClick()
            else
                this.finish()
        } else {
            if (childFragment is ExercisesResultFragment)
                this.finish()
            else if (childFragment is NavigatableFragment)
                childFragment.onBackClick()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (exercisesViewModel?.timeCounterIsPaused != true) {
            if (hasFocus) {
                if (exercisesViewModel?.secondsCounterIsStopped() == true)
                    exercisesViewModel?.startSecondsCounter()
            } else {
                exercisesViewModel?.stopSecondsCounter()
            }
        }
    }

    private fun onBackClick() {
        val fragment =
            ExercisesCloseDialogAlertFragment()
        fragment.show(supportFragmentManager, null)
        fragment.dialog?.setOnShowListener {
            exercisesViewModel?.stopSecondsCounter()
        }
    }

}