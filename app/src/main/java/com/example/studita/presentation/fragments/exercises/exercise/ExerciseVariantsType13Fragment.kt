package com.example.studita.presentation.fragments.exercises.exercise

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat.setTextAppearance
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.presentation.model.ExerciseOperatorUiModel
import com.example.studita.presentation.model.ExerciseShapeUiModel
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.views.SquareView
import com.example.studita.utils.dpToPx
import com.example.studita.utils.makeView
import com.example.studita.utils.postExt
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_linear_fragment.*


class ExerciseVariantsType13Fragment :
    ExerciseVariantsFragment(R.layout.exercise_variants_linear_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel?.let { vm ->
            when (vm.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType13UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType13UiModel
                    exerciseVariantsLinearFragmentSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                    fillLinearLayout(exerciseUiModel)
                }
            }

            if (selectedPos != -1)
                exerciseVariantsLinearFragmentCenterLinearLayout.postExt<ViewGroup> {
                    selectVariant(it, selectedPos)
                }
            observeAnswered(vm, exerciseVariantsLinearFragmentCenterLinearLayout)
        }
    }

    private fun fillVariants(variants: List<String>) {
        variants.forEach { variant ->
            val variantView =
                exerciseVariantsLinearFragmentCenterLinearLayout.makeView(R.layout.exercise_variant_text_item)
            variantView.exerciseVariantTextItem.text = variant
            variantView.setOnClickListener {
                exercisesViewModel?.let { viewModel ->
                    selectedPos =
                        exerciseVariantsLinearFragmentCenterLinearLayout.indexOfChild(it)
                    selectVariant(
                        exerciseVariantsLinearFragmentCenterLinearLayout,
                        selectedPos
                    )
                    viewModel.exerciseRequestData = ExerciseRequestData(variant)
                }
            }
            exerciseVariantsLinearFragmentCenterLinearLayout.addView(variantView)
        }
    }

    private fun fillLinearLayout(exerciseUiModel: ExerciseUiModel.ExerciseUiModelExercise.ExerciseType13UiModel) {
        (exerciseUiModel.exerciseShapeEquationUiModel).forEach {
            if (it is ExerciseShapeUiModel) {
                (0 until it.count).forEach { _ ->
                    val shapeView =
                        SquareView(exerciseVariantsLinearFragmentTopFlexboxLayout.context)
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.height = 32.dpToPx()
                    params.width = 32.dpToPx()
                    shapeView.layoutParams = params
                    shapeView.background = ContextCompat.getDrawable(
                        exerciseVariantsLinearFragmentTopFlexboxLayout.context,
                        R.drawable.exercise_rectangle_green
                    )
                    exerciseVariantsLinearFragmentTopFlexboxLayout.addView(shapeView)
                }
            } else {
                val operatorView = TextView(exerciseVariantsLinearFragmentTopFlexboxLayout.context)
                setTextAppearance(operatorView, R.style.Medium24)
                with(operatorView) {
                    val params = ViewGroup.MarginLayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.marginStart = (-4).dpToPx()
                    params.marginEnd = (-4).dpToPx()
                    layoutParams = params
                    text = (it as ExerciseOperatorUiModel).operator.toString()
                }
                exerciseVariantsLinearFragmentTopFlexboxLayout.addView(operatorView)
            }
        }
    }
}