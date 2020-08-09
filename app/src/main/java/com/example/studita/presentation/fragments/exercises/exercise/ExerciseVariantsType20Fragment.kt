package com.example.studita.presentation.fragments.exercises.exercise

import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.lifecycle.Observer
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseData
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.utils.postExt
import kotlinx.android.synthetic.main.exercise_title_number_number_name_true_false.*
import kotlinx.android.synthetic.main.exercise_title_number_number_name_variants.*
import kotlinx.android.synthetic.main.exercise_variants_title_with_images_true_false.*

class ExerciseVariantsType20Fragment :
    ExerciseVariantsFragment(R.layout.exercise_title_number_number_name_true_false) {

    private var isBonus = false

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

            isBonus =
                (exercisesViewModel?.exerciseData as? ExerciseData.ExerciseDataExercise)?.isBonus == true

            observeAnswered(vm, exerciseTitleNumberNumberNameTrueFalseLinearLayout)

            if (!vm.isBonusCompleted) {
                vm.exerciseBonusResultState.observe(
                    viewLifecycleOwner,
                    Observer { answerIsCorrect ->
                        if (answerIsCorrect != null) {
                            with(
                                getSelectedChild(
                                    exerciseTitleNumberNumberNameTrueFalseLinearLayout
                                ) as TextView
                            ) {
                                isActivated = answerIsCorrect
                                (background as TransitionDrawable).startTransition(
                                    resources.getInteger(R.integer.button_transition_duration)
                                )
                                setTextColor(ContextCompat.getColor(view.context, R.color.white))
                            }
                        }
                    })
            }
        }

        if (selectedPos != -1) {
            if (!isBonus)
                exerciseTitleNumberNumberNameTrueFalseLinearLayout.postExt<ViewGroup> {
                    selectVariant(it, selectedPos)
                }
        }
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