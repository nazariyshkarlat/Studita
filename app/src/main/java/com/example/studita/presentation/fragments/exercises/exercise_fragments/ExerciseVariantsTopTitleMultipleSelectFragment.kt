package com.example.studita.presentation.fragments.exercises.exercise_fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseData.ExerciseDataExercise.ExerciseType15Data.Companion.MAX_SELECTED_COUNT
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.domain.entity.exercise.ExerciseVariantData
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.utils.makeView
import com.example.studita.utils.postExt
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_title_layout.*

class ExerciseVariantsTopTitleMultipleSelectFragment :
    ExerciseMultipleVariantsFragment(R.layout.exercise_variants_title_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel?.let { vm ->
            when (vm.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType15UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType15UiModel
                    exerciseVariantsTitleLayoutTitle.text = exerciseUiModel.title
                    exerciseVariantsTitleLayoutSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                }
            }
            observeAnswered(vm, exerciseVariantsTitleLayoutLinearLayout)
        }

        selectCurrentVariants(exerciseVariantsTitleLayoutLinearLayout)
    }

    private fun fillVariants(variants: List<ExerciseVariantData>) {
        variants.forEach { variant ->
            val variantView =
                exerciseVariantsTitleLayoutLinearLayout.makeView(R.layout.exercise_variant_text_item)
            variantView.exerciseVariantTextItem.text = variant.variantText
            variantView.setOnClickListener { clickedView ->
                if (!clickedView.isSelected) {
                    selectVariant(
                        exerciseVariantsTitleLayoutLinearLayout,
                        exerciseVariantsTitleLayoutLinearLayout.indexOfChild(clickedView),
                        MAX_SELECTED_COUNT
                    )
                } else {
                    unSelectVariant(
                        exerciseVariantsTitleLayoutLinearLayout,
                        exerciseVariantsTitleLayoutLinearLayout.indexOfChild(clickedView)
                    )
                }
                exercisesViewModel?.exerciseRequestData = ExerciseRequestData(
                    selectedPositions.joinToString(
                        ","
                    ) {
                        variants.map {variant-> variant.meta ?: variant.variantText }[it]
                    })
            }
            exerciseVariantsTitleLayoutLinearLayout.addView(variantView)
        }
    }

}