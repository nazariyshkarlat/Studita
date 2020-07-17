package com.example.studita.presentation.fragments.exercises.exercise

import android.os.Bundle
import android.view.View
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.utils.makeView
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_title_fragment.*

class ExerciseVariantsType15Fragment : ExerciseMultipleVariantsFragment(R.layout.exercise_variants_title_fragment){

    companion object{
        const val MAX_SELECTED_COUNT = 2
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel?.let {
            observeAnswered(it, exerciseVariantsTitleFragmentLinearLayout)
            when (it.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType15UiModel -> {
                    val exerciseUiModel =
                        it.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType15UiModel
                    exerciseVariantsTitleFragmentTitle.text = exerciseUiModel.title
                    exerciseVariantsTitleFragmentSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                }
            }
            selectedPositions.forEach {position->
                selectVariant(exerciseVariantsTitleFragmentLinearLayout, position, MAX_SELECTED_COUNT)
            }
        }
    }

    private fun fillVariants(variants: List<String>){
        variants.forEach { variant ->
            val variantView = exerciseVariantsTitleFragmentLinearLayout.makeView(R.layout.exercise_variant_text_item)
            variantView.exerciseVariantTextItem.text = variant
            variantView.setOnClickListener {clickedView->
                if(!clickedView.isSelected) {
                    selectVariant(
                        exerciseVariantsTitleFragmentLinearLayout,
                        exerciseVariantsTitleFragmentLinearLayout.indexOfChild(clickedView),
                        MAX_SELECTED_COUNT
                    )
                }else{
                    unSelectVariant(
                        exerciseVariantsTitleFragmentLinearLayout,
                        exerciseVariantsTitleFragmentLinearLayout.indexOfChild(clickedView))
                }
                exercisesViewModel?.exerciseRequestData = ExerciseRequestData(
                    selectedPositions.joinToString(
                        ","
                    ) { variants[it] })
            }
            exerciseVariantsTitleFragmentLinearLayout.addView(variantView)
        }
    }

}