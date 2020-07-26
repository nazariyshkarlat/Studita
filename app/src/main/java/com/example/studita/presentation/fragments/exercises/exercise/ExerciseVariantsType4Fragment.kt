package com.example.studita.presentation.fragments.exercises.exercise

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.utils.makeView
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.utils.postExt
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_linear_fragment.*
import kotlinx.android.synthetic.main.exercise_variants_title_fragment.*

class ExerciseVariantsType4Fragment : ExerciseVariantsFragment(R.layout.exercise_variants_title_fragment){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel?.let {vm->
            observeAnswered(vm, exerciseVariantsTitleFragmentLinearLayout)
            when (vm.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType4UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType4UiModel
                    exerciseVariantsTitleFragmentTitle.text = exerciseUiModel.title.characterName
                    exerciseVariantsTitleFragmentSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                }
            }

            if (selectedPos != -1)
                exerciseVariantsTitleFragmentLinearLayout.postExt {
                    it as ViewGroup
                    selectVariant(it, selectedPos)
                }
            observeAnswered(vm, exerciseVariantsLinearFragmentCenterLinearLayout)
        }
    }

    private fun fillVariants(variants: List<String>){
        variants.forEach { variant ->
            val variantView = exerciseVariantsTitleFragmentLinearLayout.makeView(R.layout.exercise_variant_text_item)
            variantView.exerciseVariantTextItem.text = variant
            variantView.setOnClickListener {
                selectVariant(exerciseVariantsTitleFragmentLinearLayout, exerciseVariantsTitleFragmentLinearLayout.indexOfChild(it))
                exercisesViewModel?.exerciseRequestData = ExerciseRequestData(variant)
            }
            exerciseVariantsTitleFragmentLinearLayout.addView(variantView)
        }
    }

}