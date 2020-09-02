package com.example.studita.presentation.fragments.exercises.exercise_fragments

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.TextViewCompat.setTextAppearance
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.domain.entity.exercise.ExerciseVariantData
import com.example.studita.presentation.model.ExerciseImagesEquationMemberUiModel
import com.example.studita.presentation.model.ExerciseImagesRowUiModel
import com.example.studita.presentation.model.ExerciseOperatorUiModel
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.views.SquareView
import com.example.studita.utils.dpToPx
import com.example.studita.utils.makeView
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_linear_layout.*


class ExerciseVariantsTopImagesEquationFragment :
    ExerciseVariantsFragment(R.layout.exercise_variants_linear_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel?.let { vm ->
            when (vm.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType13UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType13UiModel
                    exerciseVariantsLinearLayoutSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                    fillLinearLayout(exerciseUiModel.exerciseImagesEquationUiModel)
                }
            }
            observeAnswered(vm, exerciseVariantsLinearLayoutCenterLinearLayout)
        }
        selectCurrentVariant(exerciseVariantsLinearLayoutCenterLinearLayout)
    }

    private fun fillVariants(variants: List<ExerciseVariantData>) {
        variants.forEach { variant ->
            val variantView =
                exerciseVariantsLinearLayoutCenterLinearLayout.makeView(R.layout.exercise_variant_text_item)
            variantView.exerciseVariantTextItem.text = variant.variantText
            variantView.setOnClickListener {
                exercisesViewModel?.let { viewModel ->
                    selectedPos =
                        exerciseVariantsLinearLayoutCenterLinearLayout.indexOfChild(it)
                    selectVariant(
                        exerciseVariantsLinearLayoutCenterLinearLayout,
                        selectedPos
                    )
                    viewModel.exerciseRequestData = ExerciseRequestData(variant.meta ?: variant.variantText)
                }
            }
            exerciseVariantsLinearLayoutCenterLinearLayout.addView(variantView)
        }
    }

    private fun fillLinearLayout(exerciseImagesEquationMembers: List<ExerciseImagesEquationMemberUiModel>) {
        exerciseImagesEquationMembers.forEach {
            if (it is ExerciseImagesRowUiModel) {
                (0 until it.count).forEach { _ ->
                    val emojiView = SquareView(exerciseVariantsLinearLayoutTopFlexboxLayout.context)
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.height = 32.dpToPx()
                    params.width = 32.dpToPx()
                    emojiView.layoutParams = params
                    emojiView.background = it.image
                    exerciseVariantsLinearLayoutTopFlexboxLayout.addView(emojiView)
                }
            } else {
                val operatorView = TextView(exerciseVariantsLinearLayoutTopFlexboxLayout.context)
                setTextAppearance(operatorView, R.style.Medium24)
                operatorView.text = (it as ExerciseOperatorUiModel).operator.toString()
                exerciseVariantsLinearLayoutTopFlexboxLayout.addView(operatorView)
            }
        }
    }
}