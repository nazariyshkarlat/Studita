package com.example.studita.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.OneShotPreDrawListener
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.extensions.*
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.ExercisesEndFragmentViewModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercises_detailed_stat_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class ExercisesDetailedStatFragment : NavigatableFragment(R.layout.exercises_detailed_stat_layout){

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

        exercisesViewModel?.showExercisesEndTextButton(false)

        val trueAnswers : Int? = arguments?.getInt("TRUE_ANSWERS")
        val falseAnswers : Int? = arguments?.getInt("FALSE_ANSWERS")
        val timeInSeconds : Int? = arguments?.getInt("PROCESS_SECONDS")
        println(trueAnswers)
        println(falseAnswers)
        timeInSeconds?.let{ time ->
            exercisesDetailedStatLayoutTimeSubtitle.text = context?.let {context->
                styleTimeText(
                    context,
                    getTimeText(getHours(time) to resources.getString(R.string.hours),
                        getMinutes(time) to resources.getString(R.string.minutes),
                        getSeconds(time) to  resources.getString(R.string.seconds))
                )
            }
        }
        trueAnswers?.let{
            exercisesDetailedStatLayoutTrueAnswersSubtitle.text = it.toString()
            exercisesDetailedStatLayoutXPSubtitle.text = (it*10).toString()
        }
        falseAnswers?.let{
            exercisesDetailedStatLayoutFalseAnswersSubtitle.text = it.toString()
        }
        toolbarLayoutTitle.text = resources.getString(R.string.stat)
        toolbarLayoutBackButton.setOnClickListener {
            activity?.onBackPressed()
        }

        changeLayoutIfScrollable()

        exercisesDetailedStatLayoutShareButton.setOnClickListener {
            activity?.shareImg(exercisesDetailedStatLayoutLinearLayout)
        }

    }

    private fun changeLayoutIfScrollable(){
        OneShotPreDrawListener.add(exercisesDetailedStatLayoutScrollView) {
            if (exercisesDetailedStatLayoutScrollView.height < exercisesDetailedStatLayoutScrollView.getChildAt(
                    0
                ).height + exercisesDetailedStatLayoutScrollView.paddingTop + exercisesDetailedStatLayoutScrollView.paddingBottom) {
                exercisesDetailedStatLayoutScrollView.setPadding(exercisesDetailedStatLayoutScrollView.paddingLeft, exercisesDetailedStatLayoutScrollView.paddingTop, exercisesDetailedStatLayoutScrollView.paddingRight, 16.dpToPx())
                exercisesEndFragmentViewModel?.setScrollViewDividerAndPadding(R.drawable.divider_top_drawable, 16.dpToPx())
            }
        }
    }

}