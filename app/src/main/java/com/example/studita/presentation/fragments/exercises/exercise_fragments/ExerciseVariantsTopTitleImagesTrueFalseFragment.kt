package com.example.studita.presentation.fragments.exercises.exercise_fragments

import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.lifecycle.Observer
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseData
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.presentation.model.ExerciseImagesRowUiModel
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.views.SquareView
import com.example.studita.utils.dpToPx
import com.example.studita.utils.postExt
import kotlinx.android.synthetic.main.exercise_variants_title_layout.*
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
            params.height = 32.dpToPx()
            params.width = 32.dpToPx()
            emojiView.layoutParams = params
            emojiView.background = exerciseImagesRowUiModel.image
            exerciseVariantsTitleWithImagesTrueFalseFlexboxLayout.addView(emojiView)
        }
    }

}