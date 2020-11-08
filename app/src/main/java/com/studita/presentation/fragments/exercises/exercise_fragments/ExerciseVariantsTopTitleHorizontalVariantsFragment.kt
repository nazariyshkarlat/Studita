package com.studita.presentation.fragments.exercises.exercise_fragments

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout.HORIZONTAL
import com.studita.R
import com.studita.domain.entity.exercise.ExerciseRequestData
import com.studita.presentation.model.ExerciseUiModel
import com.studita.utils.makeView
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_title_layout.*

class ExerciseVariantsTopTitleHorizontalVariantsFragment :
    ExerciseVariantsFragment(R.layout.exercise_variants_title_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel?.let { vm ->
            when (vm.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType6UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType6UiModel
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

        if(variants.size == 2)
            exerciseVariantsTitleLayoutLinearLayout.orientation = HORIZONTAL

        variants.forEach { variant ->
            val variantView =
                exerciseVariantsTitleLayoutLinearLayout.makeView(R.layout.exercise_variant_text_item)
            variantView.exerciseVariantTextItem.text = variant
            variantView.setOnClickListener {
                selectVariant(
                    exerciseVariantsTitleLayoutLinearLayout,
                    exerciseVariantsTitleLayoutLinearLayout.indexOfChild(it)
                )
                exercisesViewModel?.exerciseRequestData = ExerciseRequestData(variant)
                if (isBonus)
                    exercisesViewModel?.checkBonusResult()
            }
            exerciseVariantsTitleLayoutLinearLayout.addView(variantView)
        }
    }

}