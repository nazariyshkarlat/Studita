package com.studita.presentation.fragments.exercises.exercise_fragments

import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.studita.R
import com.studita.domain.entity.exercise.ExerciseData
import com.studita.presentation.fragments.base.NavigatableFragment
import com.studita.presentation.view_model.ExercisesViewModel
import com.studita.utils.disableAllItems
import com.studita.utils.postExt
import kotlinx.android.synthetic.main.exercise_variants_title_layout.*

open class ExerciseVariantsFragment(viewId: Int) : NavigatableFragment(viewId) {

    protected var exercisesViewModel: ExercisesViewModel? = null
    protected var isBonus = false

    var selectedPos = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        if (savedInstanceState != null)
            selectedPos = savedInstanceState.getInt("SELECTED_POSITION")

        isBonus =
            (exercisesViewModel?.exerciseData as? ExerciseData.ExerciseDataExercise)?.isBonus == true
    }

    private fun View.refreshVariants() {
        this.isEnabled = true
        this.isSelected = false
        if (this is ViewGroup) {
            for (i in 0 until this.childCount) {
                val child = this.getChildAt(i)
                child.refreshVariants()
            }
        }
    }

    protected fun observeAnswered(viewModel: ExercisesViewModel, variantsLayout: LinearLayout) {
        viewModel.answered.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { answered ->
                if (answered) {
                    variantsLayout.post {
                        variantsLayout.disableAllItems()
                    }
                }
            })
    }

    protected fun selectCurrentVariant(variantsLayout: ViewGroup){
        if (selectedPos != -1) {
            if (!isBonus)
                variantsLayout.postExt<ViewGroup> {
                    selectVariant(it, selectedPos)
                }
        }
    }

    protected fun observeBonus(variantsLayout: ViewGroup){
        if (exercisesViewModel?.isBonusCompleted == false) {
            exercisesViewModel?.exerciseBonusResultState?.observe(
                viewLifecycleOwner,
                Observer { answerIsCorrect ->
                    if (answerIsCorrect != null) {
                        with(
                            getSelectedChild(
                                variantsLayout
                            ) as TextView
                        ) {
                            isActivated = answerIsCorrect
                            (background as TransitionDrawable).startTransition(
                                resources.getInteger(R.integer.button_transition_duration)
                            )
                            setTextColor(ContextCompat.getColor(variantsLayout.context, R.color.white))
                        }
                    }
                })
        }
    }

    private fun getSelectedChild(centerLayout: ViewGroup): View =
        if (centerLayout.getChildAt(0) !is LinearLayout) centerLayout.getChildAt(selectedPos) else (centerLayout.getChildAt(
            selectedPos / 2
        ) as ViewGroup).getChildAt(selectedPos % 2)

    fun selectVariant(centerLayout: ViewGroup, position: Int) {
        val childView =
            if (centerLayout.getChildAt(0) !is LinearLayout) centerLayout.getChildAt(position) else (centerLayout.getChildAt(
                position / 2
            ) as ViewGroup).getChildAt(position % 2)
        exercisesViewModel?.setButtonEnabled(true)
        centerLayout.refreshVariants()
        childView.isEnabled = false
        childView.isSelected = true
        selectedPos = position
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("SELECTED_POSITION", selectedPos)
    }
}