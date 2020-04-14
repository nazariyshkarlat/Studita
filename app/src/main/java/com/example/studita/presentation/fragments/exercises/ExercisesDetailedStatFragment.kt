package com.example.studita.presentation.fragments.exercises

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.view.OneShotPreDrawListener
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.utils.*
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.ExercisesEndFragmentViewModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercises_detailed_stat_layout.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class ExercisesDetailedStatFragment : NavigatableFragment(R.layout.exercises_detailed_stat_layout), ViewTreeObserver.OnScrollChangedListener{

    var exercisesViewModel: ExercisesViewModel? = null
    private var exercisesEndFragmentViewModel: ExercisesEndFragmentViewModel? = null

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
        val timeInSeconds : Long? = arguments?.getLong("PROCESS_SECONDS")
        val obtainedXP : Int? = arguments?.getInt("OBTAINED_XP")
        println(trueAnswers)
        println(falseAnswers)
        timeInSeconds?.let{ time ->
            exercisesDetailedStatLayoutTimeSubtitle.text = context?.let {context->
                TimeUtils.styleTimeText(
                    context,
                    TimeUtils.getTimeText(TimeUtils.getHours(time) to resources.getString(R.string.hours),
                        TimeUtils.getMinutes(time) to resources.getString(R.string.minutes),
                        TimeUtils.getSeconds(time) to  resources.getString(R.string.seconds))
                )
            }
        }
        trueAnswers?.let{
            exercisesDetailedStatLayoutTrueAnswersSubtitle.text = it.toString()
        }
        falseAnswers?.let{
            exercisesDetailedStatLayoutFalseAnswersSubtitle.text = it.toString()
        }
        obtainedXP?.let{
            exercisesDetailedStatLayoutXPSubtitle.text = (obtainedXP).toString()
        }
        toolbarLayoutTitle.text = resources.getString(R.string.stat)
        toolbarLayoutBackButton.setOnClickListener {
            activity?.onBackPressed()
        }

        changeLayoutIfScrollable()

        exercisesDetailedStatLayoutShareButton.setOnClickListener {
        }

        exercisesDetailedStatLayoutScrollView.viewTreeObserver.addOnScrollChangedListener(this)

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

    override fun onDestroyView() {
        exercisesDetailedStatLayoutScrollView.viewTreeObserver
            .removeOnScrollChangedListener(this)
        super.onDestroyView()
    }

    override fun onScrollChanged() {
        val scrollY: Int = exercisesDetailedStatLayoutScrollView.scrollY
        toolbarLayout.background = if (scrollY != 0) context?.getDrawable(R.drawable.divider_bottom_drawable) else null
    }

}