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

    var selectedPos = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        if(savedInstanceState != null)
            selectedPos = savedInstanceState.getInt("SELECTED_POSITION")
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
                println(answered)
                if (answered) {
                    variantsLayout.post {
                        variantsLayout.disableAllItems()
                    }
                }
            })
    }

    fun selectVariant(centerLayout: ViewGroup, position: Int){
        val childView= if(centerLayout.getChildAt(0) !is LinearLayout) centerLayout.getChildAt(position) else (centerLayout.getChildAt(position/2)as ViewGroup).getChildAt(position%2)
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