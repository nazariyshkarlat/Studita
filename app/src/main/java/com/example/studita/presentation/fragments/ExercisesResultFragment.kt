package com.example.studita.presentation.fragments

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.view.OneShotPreDrawListener
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.utils.animateProgress
import com.example.studita.presentation.utils.dpToPx
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.view_model.ExercisesEndFragmentViewModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercises_result_layout.*


class ExercisesResultFragment : BaseFragment(R.layout.exercises_result_layout){

    var exercisesViewModel: ExercisesViewModel? = null
    var exercisesEndFragmentViewModel: ExercisesEndFragmentViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesEndFragmentViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesEndFragmentViewModel::class.java)
        }

        val percent : Float? = arguments?.getFloat("ANSWERS_PERCENT")
        val trueAnswers : Int? = arguments?.getInt("TRUE_ANSWERS")
        percent?.let{
            Handler().postDelayed( {
                exercisesResultLayoutProgressBar.animateProgress(percent, duration = resources.getInteger(R.integer.exercises_progress_XP_duration).toLong())
            }, resources.getInteger(R.integer.exercises_progress_XP_delay).toLong())
            exercisesResultLayoutAnswersPercent.text = resources.getString(R.string.answers_percent, (percent*100).toInt())
        }
        trueAnswers?.let{
            exercisesResultLayoutProgressBarText.text = resources.getString(R.string.exercises_XP_result, it*10)
        }

        checkScrollable()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){
            exercisesViewModel?.showExercisesEndTextButton(true)
            checkScrollable()
        }
    }

    private fun checkScrollable(){
        OneShotPreDrawListener.add(exercisesResultLayoutScrollView) {
            val isScrollable: Boolean =
                exercisesResultLayoutScrollView.height < exercisesResultLayoutScrollView.getChildAt(
                    0
                ).height + exercisesResultLayoutScrollView.paddingTop + exercisesResultLayoutScrollView.paddingBottom
            if (isScrollable) {
                exercisesEndFragmentViewModel?.setScrollViewDividerAndPadding(
                    R.drawable.divider_top_drawable,
                    8.dpToPx()
                )
                exercisesResultLayoutScrollView.setPadding(
                    exercisesResultLayoutScrollView.paddingLeft,
                    16.dpToPx(),
                    exercisesResultLayoutScrollView.paddingRight,
                    16.dpToPx()
                )
            }
        }
    }

}