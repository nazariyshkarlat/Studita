package com.example.studita.presentation.fragments.exercise

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.OneShotPreDrawListener
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.presentation.extensions.makeView
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercise_variant.view.*
import kotlinx.android.synthetic.main.exercise_variants_fragment.*


class ExerciseVariantsFragment : BaseFragment(R.layout.exercise_variants_fragment) {

    private var exercisesViewModel: ExercisesViewModel? = null

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
                        exerciseVariantsFragmentLinearLayout.disableAllItems()
                })
        }

        when (val exerciseUiModel = arguments?.getParcelable<ExerciseUiModel>("EXERCISE")) {
            is ExerciseUiModel.ExerciseUi1 -> {
                exerciseVariantsFragmentTextView.text = resources.getString(
                    R.string.exercise_type_1_title,
                    exerciseUiModel.equation[0],
                    exerciseUiModel.equation[1]
                )
                fillLinearLayout(exerciseUiModel.variants)
            }
            is ExerciseUiModel.ExerciseUi2 -> {
                exerciseVariantsFragmentTextView.text =
                    resources.getString(R.string.exercise_type_2_3_title, exerciseUiModel.equation)
                fillLinearLayout(exerciseUiModel.variants)
            }
            is ExerciseUiModel.ExerciseUi5 -> {
                exerciseVariantsFragmentTextView.text = resources.getString(
                    R.string.exercise_type_5_title,
                    exerciseUiModel.expressionParts[0],
                    exerciseUiModel.expressionParts[1],
                    exerciseUiModel.expressionResult
                )
                exerciseVariantsFragmentLinearLayout.orientation = LinearLayout.HORIZONTAL
                fillLinearLayout(exerciseUiModel.variants)
            }
            is ExerciseUiModel.ExerciseUi7 -> {
                exerciseVariantsFragmentTextView.text = resources.getString(
                    R.string.exercise_type_7_title,
                    exerciseUiModel.expressionParts[0],
                    exerciseUiModel.expressionParts[1],
                    exerciseUiModel.expressionResult
                )
                fillLinearLayout(exerciseUiModel.variants)
            }
        }

        OneShotPreDrawListener.add(exerciseVariantsFragmentScrollView) {
            val isScrollable: Boolean =
                exerciseVariantsFragmentScrollView.height < exerciseVariantsFragmentScrollView.getChildAt(
                    0
                ).height + exerciseVariantsFragmentScrollView.paddingTop + exerciseVariantsFragmentScrollView.paddingBottom
            if (isScrollable)
                exerciseVariantsFragmentScrollView.background =
                    context?.getDrawable(R.drawable.divider_top_bottom_drawable)
        }

        exercisesViewModel?.selectedPos?.let {
            if(it != -1)
                selectVariant(it)
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

    private fun fillLinearLayout(variants: List<String>){
        for(variant in variants) {
            val variantView = exerciseVariantsFragmentLinearLayout.makeView(R.layout.exercise_variant)
            variantView.exerciseVariantItem.text = variant
            variantView.setOnClickListener {
                exercisesViewModel?.selectedPos = exerciseVariantsFragmentLinearLayout.indexOfChild(it)
                exercisesViewModel?.selectedPos?.let { it1 -> selectVariant(it1) }
                when (variant) {
                    resources.getString(R.string.true_variant) -> exercisesViewModel?.exerciseRequestData = ExerciseRequestData("true")
                    resources.getString(R.string.false_variant) -> exercisesViewModel?.exerciseRequestData = ExerciseRequestData("false")
                    else -> exercisesViewModel?.exerciseRequestData = ExerciseRequestData(variant)
                }
            }
            exerciseVariantsFragmentLinearLayout.addView(variantView)
        }
    }

    private fun selectVariant(position: Int){
        val childView = exerciseVariantsFragmentLinearLayout.getChildAt(position)
        exercisesViewModel?.setButtonEnabled(true)
        exerciseVariantsFragmentLinearLayout.refreshVariants()
        childView.isEnabled = false
        childView.isSelected = true
    }
}