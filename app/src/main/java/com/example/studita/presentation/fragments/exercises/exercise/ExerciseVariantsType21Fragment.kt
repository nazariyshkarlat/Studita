package com.example.studita.presentation.fragments.exercises.exercise

import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.lifecycle.Observer
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseData
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.domain.entity.exercise.ExerciseSymbolData
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.utils.makeView
import com.example.studita.utils.postExt
import kotlinx.android.synthetic.main.exercise_title_number_number_name_variants.*
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*

class ExerciseVariantsType21Fragment :
    ExerciseVariantsFragment(R.layout.exercise_title_number_number_name_variants) {

    private var isBonus = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel?.let { vm ->
            when (vm.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType21UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType21UiModel
                    exerciseTitleNumberNumberNameVariantsSymbolTitle.text = exerciseUiModel.title.symbol
                    exerciseTitleNumberNumberNameVariantsSymbolNameTitle.text = exerciseUiModel.title.symbolName
                    fillVariants(exerciseUiModel.variants)
                }
            }

            isBonus =
                (exercisesViewModel?.exerciseData as? ExerciseData.ExerciseDataExercise)?.isBonus == true

            observeAnswered(vm, exerciseTitleNumberNumberNameVariantsLinearLayout)

            if (!vm.isBonusCompleted) {
                vm.exerciseBonusResultState.observe(
                    viewLifecycleOwner,
                    Observer { answerIsCorrect ->
                        if (answerIsCorrect != null) {
                            with(
                                getSelectedChild(
                                    exerciseTitleNumberNumberNameVariantsLinearLayout
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
                exerciseTitleNumberNumberNameVariantsLinearLayout.postExt<ViewGroup> {
                    selectVariant(it, selectedPos)
                }
        }
    }

    private fun fillVariants(variants: List<ExerciseSymbolData>) {
        variants.forEach { variant ->
            val variantView =
                exerciseTitleNumberNumberNameVariantsLinearLayout.makeView(R.layout.exercise_variant_text_item)
            variantView.exerciseVariantTextItem.text = variant.symbolName
            variantView.setOnClickListener {
                selectVariant(
                    exerciseTitleNumberNumberNameVariantsLinearLayout,
                    exerciseTitleNumberNumberNameVariantsLinearLayout.indexOfChild(it)
                )
                exercisesViewModel?.exerciseRequestData =
                    ExerciseRequestData(variant.symbol)
                if (isBonus)
                    exercisesViewModel?.checkBonusResult()
            }
            exerciseTitleNumberNumberNameVariantsLinearLayout.addView(variantView)
        }
    }

}