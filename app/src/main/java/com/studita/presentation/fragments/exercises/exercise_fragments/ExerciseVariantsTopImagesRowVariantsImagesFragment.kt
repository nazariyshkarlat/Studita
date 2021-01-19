package com.studita.presentation.fragments.exercises.exercise_fragments

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.studita.R
import com.studita.domain.entity.exercise.ExerciseRequestData
import com.studita.presentation.model.ExerciseImagesEquationMemberUiModel
import com.studita.presentation.model.ExerciseImagesRowUiModel
import com.studita.presentation.model.ExerciseOperatorUiModel
import com.studita.presentation.model.ExerciseUiModel
import com.studita.presentation.views.SquareView
import com.studita.utils.makeView
import com.google.android.flexbox.FlexboxLayout
import com.studita.utils.dp
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_linear_layout.*

class ExerciseVariantsTopImagesRowVariantsImagesFragment :
    ExerciseVariantsFragment(R.layout.exercise_variants_linear_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel?.let { vm ->
            when (vm.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType17UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType17UiModel
                    exerciseVariantsLinearLayoutSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                    fillLinearLayout(exerciseUiModel.title)
                }
            }
            observeAnswered(vm, exerciseVariantsLinearLayoutCenterLinearLayout)
        }
    }

    private fun fillVariants(variants: List<ExerciseImagesRowUiModel>) {
        variants.forEach { variant ->
            val variantView: View
            if (variant.count == 0) {
                variantView =
                    exerciseVariantsLinearLayoutCenterLinearLayout.makeView(R.layout.exercise_variant_text_item) as TextView
                variantView.exerciseVariantTextItem.text =
                    resources.getString(R.string.exercise_image_0_count)
            } else {
                variantView =
                    exerciseVariantsLinearLayoutCenterLinearLayout.makeView(R.layout.exercise_variant_linear_item) as FlexboxLayout
                for (i in 0 until variant.count) {
                    val emojiView = SquareView(exerciseVariantsLinearLayoutCenterLinearLayout.context)
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.height = 24F.dp
                    params.width = 24F.dp
                    emojiView.layoutParams = params
                    emojiView.background = variant.image
                    variantView.addView(emojiView)
                }
            }
            variantView.setOnClickListener {
                selectVariant(
                    exerciseVariantsLinearLayoutCenterLinearLayout,
                    exerciseVariantsLinearLayoutCenterLinearLayout.indexOfChild(it)
                )
                exercisesViewModel?.exerciseRequestData =
                    ExerciseRequestData(variant.count.toString())
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
                    params.height = 32F.dp
                    params.width = 32F.dp
                    emojiView.layoutParams = params
                    emojiView.background = it.image
                    exerciseVariantsLinearLayoutTopFlexboxLayout.addView(emojiView)
                }
            } else {
                val operatorView = TextView(exerciseVariantsLinearLayoutTopFlexboxLayout.context)
                TextViewCompat.setTextAppearance(operatorView, R.style.Medium24)
                operatorView.text = (it as ExerciseOperatorUiModel).operator.toString()
                exerciseVariantsLinearLayoutTopFlexboxLayout.addView(operatorView)
            }
        }
    }
}