package com.example.studita.presentation.activities

import android.os.Bundle
import android.view.MotionEvent
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.utils.navigateTo
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.fragments.exercises.*
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
                exercisesViewModel?.let {
                    it.chapterPartsCount = extras.getInt("CHAPTER_PARTS_COUNT")
                    it.chapterPartNumber = extras.getInt("CHAPTER_PART_NUMBER")
                    it.chapterNumber = extras.getInt("CHAPTER_NUMBER")
                    it.isTraining = extras.getBoolean("IS_TRAINING")
                    it.getExercises(it.chapterPartNumber)
                }
            }
            navigateTo(ExercisesLoadFragment(), R.id.frameLayout)
        }
    }

    override fun onBackPressed() {
        val childFragment =  supportFragmentManager.findFragmentById(R.id.exercisesEndLayoutFrameLayout)
        if(childFragment == null) {
            if(exercisesViewModel?.exercisesResultSentToServer() == false)
                onBackClick()
            else
                this.finish()
        }else{
            if(childFragment is ExercisesResultFragment)
                this.finish()
            else if(childFragment is NavigatableFragment)
                childFragment.onBackClick()
        }
    }

   override fun onWindowFocusChanged(hasFocus: Boolean){
       super.onWindowFocusChanged(hasFocus)
       if(exercisesViewModel?.timeCounterIsPaused != true) {
           if (hasFocus) {
               if (exercisesViewModel?.secondsCounterIsStopped() == true)
                   exercisesViewModel?.startSecondsCounter()
           } else {
               exercisesViewModel?.stopSecondsCounter()
           }
       }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val fragment = supportFragmentManager.findFragmentById(R.id.frameLayout)
        if(fragment is DispatchTouchEvent){
            fragment.dispatchTouchEvent(ev)
        }
        return super.dispatchTouchEvent(ev)
    }

    interface DispatchTouchEvent{
        fun dispatchTouchEvent(ev: MotionEvent): Boolean
    }

    private fun onBackClick(){
        val fragment = ExercisesCloseDialogAlertFragment()
            fragment.show(supportFragmentManager, null)
            fragment.dialog?.setOnShowListener {
                exercisesViewModel?.stopSecondsCounter()
            }
    }

}