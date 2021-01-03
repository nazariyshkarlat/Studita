package com.studita.presentation.activities

import android.os.Bundle
import android.view.MotionEvent
import androidx.lifecycle.*
import com.studita.R
import com.studita.presentation.fragments.base.NavigatableFragment
import com.studita.presentation.fragments.dialog_alerts.ExercisesCloseDialogAlertFragment
import com.studita.presentation.fragments.exercises.ExercisesLoadFragment
import com.studita.presentation.fragments.exercises.ExercisesResultFragment
import com.studita.presentation.view_model.ChapterViewModel
import com.studita.presentation.view_model.ExercisesViewModel
import com.studita.utils.addFragment
import com.studita.utils.navigateTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

class ExercisesActivity : DefaultActivity() {

    private var exercisesViewModel: ExercisesViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frame_layout)

        exercisesViewModel =  ViewModelProviders.of(this, object : AbstractSavedStateViewModelFactory(this, null) {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                return ExercisesViewModel(application, intent!!.extras!!.getInt("CHAPTER_PART_NUMBER"), handle) as T
            }
        })[ExercisesViewModel::class.java]

        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras != null) {
                exercisesViewModel?.let {
                    it.chapterPartsInChapterCount = extras.getInt("CHAPTER_PARTS_IN_CHAPTER_COUNT")
                    it.chapterNumber = extras.getInt("CHAPTER_NUMBER")
                    it.exercisesInChapterCount = extras.getInt("EXERCISES_IN_CHAPTER_COUNT")
                    it.chapterName = extras.getString("CHAPTER_NAME")
                    it.chapterPartInChapterNumber = extras.getInt("CHAPTER_PART_IN_CHAPTER_NUMBER")
                    it.isTraining = extras.getBoolean("IS_TRAINING")
                }
            }
            addFragment(ExercisesLoadFragment(), R.id.frameLayout)
        }
    }

    override fun onBackPressed() {
        val childFragment =
            supportFragmentManager.findFragmentById(R.id.exercisesEndLayoutFrameLayout)
        if (childFragment == null) {
            if (exercisesViewModel?.exercisesResultSentToServer() == false && exercisesViewModel?.exerciseIndex != 0)
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


    override fun onSaveInstanceState(outState: Bundle) {
        exercisesViewModel?.saveState()
        super.onSaveInstanceState(outState)
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