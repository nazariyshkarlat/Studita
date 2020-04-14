package com.example.studita.presentation.fragments.exercises.exercise

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.view.OneShotPreDrawListener
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.ExercisesViewModel

open class ExerciseVariantsFragment(viewId: Int) : NavigatableFragment(viewId){

    protected var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

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

    private fun View.disableAllItems(){
        this.isEnabled = false
        if (this is ViewGroup) {
            for (i in 0 until this.childCount) {
                val child = this.getChildAt(i)
                child.disableAllItems()
            }
        }
    }

    protected fun observeAnswered(viewModel: ExercisesViewModel, variantsLayout: LinearLayout){
        viewModel.answered.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { answered ->
                if (answered)
                    variantsLayout.disableAllItems()
            })
    }

    fun selectVariant(centerLayout: ViewGroup, position: Int){
        val childView = centerLayout.getChildAt(position)
        exercisesViewModel?.setButtonEnabled(true)
        centerLayout.refreshVariants()
        childView.isEnabled = false
        childView.isSelected = true
    }

}