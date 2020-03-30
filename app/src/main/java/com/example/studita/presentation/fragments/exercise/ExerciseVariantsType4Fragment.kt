package com.example.studita.presentation.fragments.exercise

import android.os.Bundle
import android.view.View
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.presentation.utils.makeView
import com.example.studita.presentation.model.ExerciseUiModel
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_title_fragment.*

class ExerciseVariantsType4Fragment : ExerciseVariantsFragment(R.layout.exercise_variants_title_fragment){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel?.let {
            observeAnswered(it, exerciseVariantsTitleFragmentLinearLayout)
            when (it.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType4UiModel -> {
                    val exerciseUiModel =
                        it.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType4UiModel
                    exerciseVariantsTitleFragmentTitle.text = exerciseUiModel.title.numberName
                    exerciseVariantsTitleFragmentSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                }
            }
            if (it.selectedPos != -1)
                selectVariant(exerciseVariantsTitleFragmentLinearLayout, it.selectedPos)
        }

        onPreDraw(exerciseVariantsTitleFragmentScrollView)
    }

    private fun fillVariants(variants: List<String>){
        for(variant in variants) {
            val variantView = exerciseVariantsTitleFragmentLinearLayout.makeView(R.layout.exercise_variant_text_item)
            variantView.exerciseVariantTextItem.text = variant
            variantView.setOnClickListener {
                exercisesViewModel?.selectedPos = exerciseVariantsTitleFragmentLinearLayout.indexOfChild(it)
                exercisesViewModel?.selectedPos?.let { it1 -> selectVariant(exerciseVariantsTitleFragmentLinearLayout, it1) }
                exercisesViewModel?.exerciseRequestData = ExerciseRequestData(variant)
            }
            exerciseVariantsTitleFragmentLinearLayout.addView(variantView)
        }
    }

}