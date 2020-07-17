package com.example.studita.presentation.fragments.exercises.exercise

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.presentation.model.ExerciseUiModel
import kotlinx.android.synthetic.main.exercise_variants_true_false.*

class ExerciseVariantsType7Fragment : ExerciseVariantsFragment(R.layout.exercise_variants_true_false){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel?.let {
            observeAnswered(it, exerciseVariantsTrueFalseLinearLayout)
            when (it.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType7UiModel -> {
                    val exerciseUiModel =
                        it.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType7UiModel
                    exerciseVariantsTrueFalseTitle.text = exerciseUiModel.title
                }
            }
            fillVariants()
            if (selectedPos != -1)
                selectVariant(exerciseVariantsTrueFalseLinearLayout, selectedPos)
        }
    }

    private fun fillVariants(){
        (exerciseVariantsTrueFalseTrue as TextView).text = resources.getString(R.string.true_variant)
        (exerciseVariantsTrueFalseFalse as TextView).text = resources.getString(R.string.false_variant)
        for(variantView in exerciseVariantsTrueFalseLinearLayout.children) {
            variantView.setOnClickListener {
                selectVariant(
                    exerciseVariantsTrueFalseLinearLayout,
                    exerciseVariantsTrueFalseLinearLayout.indexOfChild(it)
                )
                exercisesViewModel?.exerciseRequestData= ExerciseRequestData(
                    if (exerciseVariantsTrueFalseLinearLayout.indexOfChild(variantView) == 0) "true" else "false"
                )
            }
        }
    }

}