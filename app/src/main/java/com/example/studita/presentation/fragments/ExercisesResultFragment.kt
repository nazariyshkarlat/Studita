package com.example.studita.presentation.fragments

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewTreeObserver.OnScrollChangedListener
import androidx.core.view.OneShotPreDrawListener
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.extensions.animateProgress
import com.example.studita.presentation.extensions.dpToPx
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.view_model.ExercisesEndFragmentViewModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercise_variants_fragment.*
import kotlinx.android.synthetic.main.exercises_result_layout.*
import kotlinx.android.synthetic.main.home_layout.*


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

        val percent : Int? = arguments?.getInt("ANSWERS_PERCENT")
        val trueAnswers : Int? = arguments?.getInt("TRUE_ANSWERS")
        percent?.let{
            Handler().postDelayed( {
                exercisesResultLayoutProgressBar.animateProgress(percent, duration = 1500L)
            }, 200)
        }
        trueAnswers?.let{
            exercisesResultLayoutAnswersPercent.text = resources.getString(R.string.answers_percent, (it*10).toString())
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