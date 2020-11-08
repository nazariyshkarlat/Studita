package com.studita.presentation.fragments.exercises.exercise_fragments

import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.studita.R
import com.studita.domain.entity.exercise.ExerciseData
import com.studita.domain.entity.exercise.ExerciseRequestData
import com.studita.domain.entity.exercise.ExerciseSymbolData
import com.studita.presentation.model.ExerciseUiModel
import com.studita.utils.makeView
import com.studita.utils.postExt
import kotlinx.android.synthetic.main.exercise_title_number_number_name_true_false.*
import kotlinx.android.synthetic.main.exercise_title_number_number_name_variants.*
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*

class ExerciseVariantsTitleNumberNumberNameFragment :
    ExerciseVariantsFragment(R.layout.exercise_title_number_number_name_variants) {

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

            observeAnswered(vm, exerciseTitleNumberNumberNameVariantsLinearLayout)
            observeBonus(exerciseTitleNumberNumberNameVariantsLinearLayout)
        }
        selectCurrentVariant(exerciseTitleNumberNumberNameVariantsLinearLayout)
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