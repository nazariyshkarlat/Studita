package com.example.studita.presentation.fragments.exercises.exercise

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.domain.entity.exercise.ExerciseSymbolData
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.utils.makeView
import com.example.studita.utils.postExt
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_title_fragment.*

class ExerciseVariantsType3Fragment :
    ExerciseVariantsFragment(R.layout.exercise_variants_title_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel?.let { vm ->
            when (vm.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType3UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType3UiModel
                    exerciseVariantsTitleFragmentTitle.text =
                        exerciseUiModel.title.symbol
                    exerciseVariantsTitleFragmentSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                }
            }

            if (selectedPos != -1)
                exerciseVariantsTitleFragmentLinearLayout.postExt<ViewGroup> {
                    selectVariant(it, selectedPos)
                }
            observeAnswered(vm, exerciseVariantsTitleFragmentLinearLayout)
        }

    }

    private fun fillVariants(variants: List<ExerciseSymbolData>) {
        variants.forEach { variant ->
            val variantView =
                exerciseVariantsTitleFragmentLinearLayout.makeView(R.layout.exercise_variant_text_item)
            variantView.exerciseVariantTextItem.text = variant.symbolName
            variantView.setOnClickListener {
                selectVariant(
                    exerciseVariantsTitleFragmentLinearLayout,
                    exerciseVariantsTitleFragmentLinearLayout.indexOfChild(it)
                )
                exercisesViewModel?.exerciseRequestData =
                    ExerciseRequestData(variant.symbol)
            }
            exerciseVariantsTitleFragmentLinearLayout.addView(variantView)
        }
    }

}