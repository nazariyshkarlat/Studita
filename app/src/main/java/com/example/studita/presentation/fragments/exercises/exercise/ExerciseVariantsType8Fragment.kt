package com.example.studita.presentation.fragments.exercises.exercise

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.utils.makeView
import com.example.studita.presentation.model.ExerciseUiModel
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_title_fragment.*

class ExerciseVariantsType8Fragment : ExerciseVariantsFragment(R.layout.exercise_variants_title_fragment){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel?.let {
            observeAnswered(it, exerciseVariantsTitleFragmentLinearLayout)
            when (it.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType8UiModel -> {
                    val exerciseUiModel =
                        it.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType8UiModel
                    exerciseVariantsTitleFragmentTitle.text = exerciseUiModel.title
                    exerciseVariantsTitleFragmentSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                }
            }
            if (it.selectedPos != -1)
                selectVariant(exerciseVariantsTitleFragmentLinearLayout, it.selectedPos)
        }

        exerciseVariantsTitleFragmentLinearLayout.orientation = LinearLayout.HORIZONTAL
    }

    private fun fillVariants(variants: List<String>){
        for(variant in variants) {
            val variantView = exerciseVariantsTitleFragmentLinearLayout.makeView(R.layout.exercise_variant_text_item)
            variantView.exerciseVariantTextItem.text = variant
            variantView.setOnClickListener {
                exercisesViewModel?.let {viewModel->
                    viewModel.selectedPos =
                        exerciseVariantsTitleFragmentLinearLayout.indexOfChild(it)
                        selectVariant(
                            exerciseVariantsTitleFragmentLinearLayout,
                            viewModel.selectedPos
                        )
                    viewModel.exerciseRequestData = ExerciseRequestData(variant)
                }
            }
            exerciseVariantsTitleFragmentLinearLayout.addView(variantView)
        }
    }

}