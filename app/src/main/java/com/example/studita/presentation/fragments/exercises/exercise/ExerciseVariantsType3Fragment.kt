package com.example.studita.presentation.fragments.exercises.exercise

import android.os.Bundle
import android.view.View
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseNumberData
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.presentation.utils.makeView
import com.example.studita.presentation.model.ExerciseUiModel
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_title_fragment.*

class ExerciseVariantsType3Fragment : ExerciseVariantsFragment(R.layout.exercise_variants_title_fragment){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel?.let {
            observeAnswered(it, exerciseVariantsTitleFragmentLinearLayout)
            when (it.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType3UiModel -> {
                    val exerciseUiModel =
                        it.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType3UiModel
                    exerciseVariantsTitleFragmentTitle.text = exerciseUiModel.title.number.toString()
                    exerciseVariantsTitleFragmentSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                }
            }
            if (it.selectedPos != -1)
                selectVariant(exerciseVariantsTitleFragmentLinearLayout, it.selectedPos)
        }

    }

    private fun fillVariants(variants: List<ExerciseNumberData>){
        for(variant in variants) {
            val variantView = exerciseVariantsTitleFragmentLinearLayout.makeView(R.layout.exercise_variant_text_item)
            variantView.exerciseVariantTextItem.text = variant.numberName
            variantView.setOnClickListener {
                exercisesViewModel?.let{viewModel->
                        viewModel.selectedPos =
                            exerciseVariantsTitleFragmentLinearLayout.indexOfChild(it)
                            selectVariant(
                                exerciseVariantsTitleFragmentLinearLayout,
                                viewModel.selectedPos
                            )
                        exercisesViewModel?.exerciseRequestData =
                            ExerciseRequestData(variant.number.toString())
                    }
            }
            exerciseVariantsTitleFragmentLinearLayout.addView(variantView)
        }
    }

}