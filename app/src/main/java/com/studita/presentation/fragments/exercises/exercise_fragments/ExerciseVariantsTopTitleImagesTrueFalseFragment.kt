package com.studita.presentation.fragments.exercises.exercise_fragments

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import com.studita.R
import com.studita.domain.entity.exercise.ExerciseRequestData
import com.studita.presentation.model.ExerciseImagesRowUiModel
import com.studita.presentation.model.ExerciseUiModel
import com.studita.presentation.views.SquareView
import com.studita.utils.dp
import kotlinx.android.synthetic.main.exercise_variants_title_with_images_true_false.*

class ExerciseVariantsTopTitleImagesTrueFalseFragment :
    ExerciseVariantsFragment(R.layout.exercise_variants_title_with_images_true_false) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel?.let { vm ->
            when (vm.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType18UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType18UiModel
                    exerciseVariantsTitleWithImagesTrueFalseTitle.text = exerciseUiModel.title
                    fillVariants()
                    fillLinearLayout(exerciseUiModel.titleImages)
                }
            }

            observeAnswered(vm, exerciseVariantsTitleWithImagesTrueFalseLinearLayout)
            observeBonus(exerciseVariantsTitleWithImagesTrueFalseLinearLayout)
        }
        selectCurrentVariant(exerciseVariantsTitleWithImagesTrueFalseLinearLayout)
    }

    private fun fillVariants() {
        (exerciseVariantsTitleWithImagesTrueFalseTrue as TextView).text =
            resources.getString(R.string.true_variant)
        (exerciseVariantsTitleWithImagesTrueFalseFalse as TextView).text =
            resources.getString(R.string.false_variant)
        for (variantView in exerciseVariantsTitleWithImagesTrueFalseLinearLayout.children) {
            variantView.setOnClickListener {
                selectVariant(
                    exerciseVariantsTitleWithImagesTrueFalseLinearLayout,
                    exerciseVariantsTitleWithImagesTrueFalseLinearLayout.indexOfChild(it)
                )
                exercisesViewModel?.exerciseRequestData = ExerciseRequestData(
                    if (exerciseVariantsTitleWithImagesTrueFalseLinearLayout.indexOfChild(
                            variantView
                        ) == 0
                    ) "true" else "false"
                )
                if (isBonus)
                    exercisesViewModel?.checkBonusResult()
            }
        }
    }

    private fun fillLinearLayout(exerciseImagesRowUiModel: ExerciseImagesRowUiModel) {
        (0 until exerciseImagesRowUiModel.count).forEach { _ ->
            val emojiView =
                SquareView(exerciseVariantsTitleWithImagesTrueFalseFlexboxLayout.context)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.height = 32F.dp
            params.width = 32F.dp
            emojiView.layoutParams = params
            emojiView.background = exerciseImagesRowUiModel.image
            exerciseVariantsTitleWithImagesTrueFalseFlexboxLayout.addView(emojiView)
        }
    }

}