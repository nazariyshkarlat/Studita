package com.example.studita.presentation.fragments.exercise

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.core.view.OneShotPreDrawListener
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercise_variants_linear_fragment.*
import kotlinx.android.synthetic.main.exercise_variants_title_fragment.*

open class ExerciseVariantsFragment(viewId: Int) : NavigatableFragment(viewId){

    protected var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesViewModel?.let {
            it.answered.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer { answered ->
                    if (answered)
                        exerciseVariantsLinearFragmentCenterLinearLayout.disableAllItems()
                })
        }
    }

    protected fun onPreDraw(scrollView: ScrollView){
        with(scrollView){
            OneShotPreDrawListener.add(this) {
                    if (this.height < this.getChildAt(
                            0
                        ).height + this.paddingTop + this.paddingBottom
                    ) {
                        this.background =
                            context?.getDrawable(R.drawable.divider_top_bottom_drawable)
                    }
            }
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

    fun selectVariant(centerLayout: ViewGroup, position: Int){
        val childView = centerLayout.getChildAt(position)
        exercisesViewModel?.setButtonEnabled(true)
        centerLayout.refreshVariants()
        childView.isEnabled = false
        childView.isSelected = true
    }

}