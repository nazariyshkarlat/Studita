package com.studita.presentation.fragments.exercises.exercise_fragments

import android.os.Bundle
import android.view.View
import com.studita.R
import com.studita.domain.entity.exercise.ExerciseRequestData
import com.studita.presentation.model.ExerciseUiModel
import com.studita.utils.makeView
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_title_layout.*

class ExerciseVariantsTopTitleFragment :
    ExerciseVariantsFragment(R.layout.exercise_variants_title_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel?.let { vm ->
            when (vm.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType3UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType3UiModel
                    exerciseVariantsTitleLayoutTitle.text =
                        exerciseUiModel.title.symbol
                    exerciseVariantsTitleLayoutSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                }
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType4UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType4UiModel
                    exerciseVariantsTitleLayoutTitle.text = exerciseUiModel.title.symbolName
                    exerciseVariantsTitleLayoutSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                }
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType5UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType5UiModel
                    exerciseVariantsTitleLayoutTitle.text = exerciseUiModel.title
                    exerciseVariantsTitleLayoutSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                }
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType24UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType24UiModel
                    exerciseVariantsTitleLayoutTitle.text = exerciseUiModel.title
                    exerciseVariantsTitleLayoutSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                }
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType25UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType25UiModel
                    exerciseVariantsTitleLayoutTitle.text = exerciseUiModel.title
                    exerciseVariantsTitleLayoutSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                }
            }

            observeAnswered(vm, exerciseVariantsTitleLayoutLinearLayout)
            observeBonus(exerciseVariantsTitleLayoutLinearLayout)

        }

        selectCurrentVariant(exerciseVariantsTitleLayoutLinearLayout)

    }

    private fun fillVariants(variants: List<String>) {
        variants.forEach { variant ->
            val variantView =
                exerciseVariantsTitleLayoutLinearLayout.makeView(R.layout.exercise_variant_text_item)
            variantView.exerciseVariantTextItem.text = variant
            variantView.setOnClickListener {
                selectVariant(
                    exerciseVariantsTitleLayoutLinearLayout,
                    exerciseVariantsTitleLayoutLinearLayout.indexOfChild(it)
                )
                exercisesViewModel?.exerciseRequestData = ExerciseRequestData(variant ?: variant)
                if (isBonus)
                    exercisesViewModel?.checkBonusResult()
            }
            exerciseVariantsTitleLayoutLinearLayout.addView(variantView)
        }
    }

}