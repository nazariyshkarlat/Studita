package com.studita.presentation.fragments.exercises.exercise_fragments

import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.lifecycle.Observer
import com.studita.R
import com.studita.domain.entity.exercise.ExerciseData
import com.studita.domain.entity.exercise.ExerciseRequestData
import com.studita.presentation.model.ExerciseUiModel
import com.studita.utils.postExt
import kotlinx.android.synthetic.main.exercise_variants_long_title_true_false.*
import kotlinx.android.synthetic.main.exercise_variants_title_layout.*
import kotlinx.android.synthetic.main.exercise_variants_true_false.*

class ExerciseVariantsTrueFalseFragment :
    ExerciseVariantsFragment(R.layout.exercise_variants_true_false) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel?.let { vm ->
            when (vm.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType7UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType7UiModel
                    exerciseVariantsTrueFalseTitle.text = exerciseUiModel.title
                    fillVariants()
                }
            }

            observeAnswered(vm, exerciseVariantsTrueFalseLinearLayout)
            observeBonus(exerciseVariantsTrueFalseLinearLayout)

        }

        selectCurrentVariant(exerciseVariantsTrueFalseLinearLayout)
    }

    private fun fillVariants() {
        (exerciseVariantsTrueFalseTrue as TextView).text =
            resources.getString(R.string.true_variant)
        (exerciseVariantsTrueFalseFalse as TextView).text =
            resources.getString(R.string.false_variant)
        for (variantView in exerciseVariantsTrueFalseLinearLayout.children) {
            variantView.setOnClickListener {
                selectVariant(
                    exerciseVariantsTrueFalseLinearLayout,
                    exerciseVariantsTrueFalseLinearLayout.indexOfChild(it)
                )
                exercisesViewModel?.exerciseRequestData = ExerciseRequestData(
                    if (exerciseVariantsTrueFalseLinearLayout.indexOfChild(
                            variantView
                        ) == 0
                    ) "true" else "false"
                )
                if (isBonus)
                    exercisesViewModel?.checkBonusResult()
            }
        }
    }

}