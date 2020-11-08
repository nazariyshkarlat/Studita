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
import kotlinx.android.synthetic.main.exercise_title_number_number_name_true_false.*
import kotlinx.android.synthetic.main.exercise_variants_title_with_images_true_false.*

class ExerciseVariantsTitleNumberNumberNameTrueFalseFragment :
    ExerciseVariantsFragment(R.layout.exercise_title_number_number_name_true_false) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel?.let { vm ->
            when (vm.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType20UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType20UiModel
                    exerciseTitleNumberNumberNameTrueFalseSymbolTitle.text = exerciseUiModel.title.symbol
                    exerciseTitleNumberNumberNameTrueFalseSymbolNameTitle.text = exerciseUiModel.title.symbolName
                    fillVariants()
                }
            }

            observeAnswered(vm, exerciseTitleNumberNumberNameTrueFalseLinearLayout)
            observeBonus(exerciseTitleNumberNumberNameTrueFalseLinearLayout)
        }
        selectCurrentVariant(exerciseTitleNumberNumberNameTrueFalseLinearLayout)
    }

    private fun fillVariants() {
        (exerciseTitleNumberNumberNameTrueFalseTrue as TextView).text =
            resources.getString(R.string.true_variant)
        (exerciseTitleNumberNumberNameTrueFalseFalse as TextView).text =
            resources.getString(R.string.false_variant)
        for (variantView in exerciseTitleNumberNumberNameTrueFalseLinearLayout.children) {
            variantView.setOnClickListener {
                selectVariant(
                    exerciseTitleNumberNumberNameTrueFalseLinearLayout,
                    exerciseTitleNumberNumberNameTrueFalseLinearLayout.indexOfChild(it)
                )
                exercisesViewModel?.exerciseRequestData = ExerciseRequestData(
                    if (exerciseTitleNumberNumberNameTrueFalseLinearLayout.indexOfChild(
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