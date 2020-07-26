package com.example.studita.presentation.fragments.exercises.exercise

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.utils.makeView
import com.example.studita.utils.postExt
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_linear_fragment.*
import kotlinx.android.synthetic.main.exercise_variants_title_fragment.*

class ExerciseVariantsType15Fragment : ExerciseMultipleVariantsFragment(R.layout.exercise_variants_title_fragment){

    companion object{
        const val MAX_SELECTED_COUNT = 2
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel?.let {vm->
            observeAnswered(vm, exerciseVariantsTitleFragmentLinearLayout)
            when (vm.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType15UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType15UiModel
                    exerciseVariantsTitleFragmentTitle.text = exerciseUiModel.title
                    exerciseVariantsTitleFragmentSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                }
            }

            exerciseVariantsTitleFragmentLinearLayout.postExt {
                it as ViewGroup
                selectedPositions.forEach { position ->
                    selectVariant(
                        it,
                        position,
                        MAX_SELECTED_COUNT
                    )
                }
            }
            observeAnswered(vm, exerciseVariantsLinearFragmentCenterLinearLayout)
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