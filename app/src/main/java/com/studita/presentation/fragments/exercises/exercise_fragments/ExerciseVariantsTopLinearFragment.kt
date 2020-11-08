package com.studita.presentation.fragments.exercises.exercise_fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.studita.R
import com.studita.domain.entity.exercise.ExerciseRequestData
import com.studita.presentation.model.ExerciseImagesRowUiModel
import com.studita.presentation.model.ExerciseUiModel
import com.studita.presentation.views.SquareView
import com.studita.utils.dpToPx
import com.studita.utils.makeView
import com.studita.utils.postExt
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_linear_layout.*
import kotlinx.android.synthetic.main.exercise_variants_title_layout.*


class ExerciseVariantsTopLinearFragment :
    ExerciseVariantsFragment(R.layout.exercise_variants_linear_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel?.let { vm ->
            when (vm.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType2UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType2UiModel
                    fillLinearLayout(exerciseUiModel.title)
                    exerciseVariantsLinearLayoutSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                }
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType14UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType14UiModel
                    fillLinearLayout(exerciseUiModel.title)
                    exerciseVariantsLinearLayoutSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                }
            }
            observeAnswered(vm, exerciseVariantsLinearLayoutCenterLinearLayout)
        }

        selectCurrentVariant(exerciseVariantsLinearLayoutCenterLinearLayout)
    }

    private fun fillVariants(variants: List<String>) {
        variants.forEach { variant ->
            val variantView =
                exerciseVariantsLinearLayoutCenterLinearLayout.makeView(R.layout.exercise_variant_text_item)
            variantView.exerciseVariantTextItem.text = variant
            variantView.setOnClickListener {
                selectVariant(
                    exerciseVariantsLinearLayoutCenterLinearLayout,
                    exerciseVariantsLinearLayoutCenterLinearLayout.indexOfChild(it)
                )
                exercisesViewModel?.exerciseRequestData = ExerciseRequestData(variant)
            }
            exerciseVariantsLinearLayoutCenterLinearLayout.addView(variantView)
        }
    }

    private fun fillLinearLayout(exerciseImagesRowUiModel: ExerciseImagesRowUiModel) {
        (0 until exerciseImagesRowUiModel.count).forEach { _ ->
            val emojiView = SquareView(exerciseVariantsLinearLayoutTopFlexboxLayout.context)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.height = 32F.dpToPx()
            params.width = 32F.dpToPx()
            emojiView.layoutParams = params
            emojiView.background = exerciseImagesRowUiModel.image
            exerciseVariantsLinearLayoutTopFlexboxLayout.addView(emojiView)
        }
    }
}