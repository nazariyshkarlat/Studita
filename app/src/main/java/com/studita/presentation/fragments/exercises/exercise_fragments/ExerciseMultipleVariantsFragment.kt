package com.studita.presentation.fragments.exercises.exercise_fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProviders
import com.studita.presentation.fragments.base.NavigatableFragment
import com.studita.presentation.view_model.ExercisesViewModel
import com.studita.utils.postExt

open class ExerciseMultipleVariantsFragment(viewId: Int) : NavigatableFragment(viewId) {

    var selectedPositions = ArrayList<Int>()
    var correctCount = 0

    protected var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        if (savedInstanceState != null)
            selectedPositions =
                savedInstanceState.getIntegerArrayList("SELECTED_POSITIONS") as ArrayList<Int>
    }

    private fun View.disableAllItems() {
        this.isEnabled = false
        if (this is ViewGroup) {
            for (i in 0 until this.childCount) {
                val child = this.getChildAt(i)
                child.disableAllItems()
            }
        }
    }

    protected fun observeAnswered(viewModel: ExercisesViewModel, variantsLayout: LinearLayout) {
        viewModel.answered.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { answered ->
                if (answered)
                    variantsLayout.disableAllItems()
            })
    }

    protected fun selectCurrentVariants(variantsLayout: LinearLayout){
        variantsLayout.postExt<ViewGroup> {
            selectedPositions.forEach { position ->
                selectVariant(
                    it,
                    position
                )
            }
        }
    }

    fun selectVariant(centerLayout: ViewGroup, position: Int) {

        if (!selectedPositions.contains(position)) {
            if (correctCount == selectedPositions.size)
                unSelectVariant(centerLayout, selectedPositions.last())
        }

        val childView =
            if (centerLayout.getChildAt(0) !is LinearLayout) centerLayout.getChildAt(position) else (centerLayout.getChildAt(
                position / 2
            ) as ViewGroup).getChildAt(position % 2)
        childView.isSelected = true

        if (!selectedPositions.contains(position))
            selectedPositions.add(position)

        if (correctCount == selectedPositions.size)
            exercisesViewModel?.setButtonEnabled(true)

        exercisesViewModel?.selectVariantEvent?.value = selectedPositions.size
    }

    fun unSelectVariant(centerLayout: ViewGroup, position: Int) {
        val childView =
            if (centerLayout.getChildAt(0) !is LinearLayout) centerLayout.getChildAt(position) else (centerLayout.getChildAt(
                position / 2
            ) as ViewGroup).getChildAt(position % 2)
        exercisesViewModel?.setButtonEnabled(false)
        childView.isSelected = false
        selectedPositions.remove(position)

        exercisesViewModel?.selectVariantEvent?.value = selectedPositions.size
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntegerArrayList("SELECTED_POSITIONS", selectedPositions)
    }
}