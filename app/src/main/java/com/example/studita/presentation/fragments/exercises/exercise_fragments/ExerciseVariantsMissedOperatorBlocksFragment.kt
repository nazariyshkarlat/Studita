package com.example.studita.presentation.fragments.exercises.exercise_fragments

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
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.utils.postExt
import kotlinx.android.synthetic.main.exercise_variants_long_title_true_false.*
import kotlinx.android.synthetic.main.exercise_variants_missed_operator_blocks_layout.*
import kotlinx.android.synthetic.main.exercise_variants_title_layout.*

class ExerciseVariantsMissedOperatorBlocksFragment : ExerciseVariantsFragment(R.layout.exercise_variants_missed_operator_blocks_layout){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel?.let { vm ->
            when (vm.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType23UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType23UiModel
                    exerciseVariantsMissedOperatorBlocksLayoutTitleFirstPart.text = exerciseUiModel.titleParts.first
                    exerciseVariantsMissedOperatorBlocksLayoutTitleSecondPart.text = exerciseUiModel.titleParts.second
                    exerciseVariantsMissedOperatorBlocksLayoutSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                }
            }
            observeAnswered(vm, exerciseVariantsMissedOperatorBlocksLayoutVariantsLinearLayout)
            observeBonus(exerciseVariantsMissedOperatorBlocksLayoutVariantsLinearLayout)
        }
        selectCurrentVariant(exerciseVariantsMissedOperatorBlocksLayoutVariantsLinearLayout)
    }

    private fun fillVariants(variants: Pair<String, String>) {
        exerciseVariantsMissedOperatorBlocksLayoutLeftVariant.text = variants.first
        exerciseVariantsMissedOperatorBlocksLayoutRightVariant.text = variants.second
        exerciseVariantsMissedOperatorBlocksLayoutVariantsLinearLayout.children.forEachIndexed{ idx, variantView ->
            variantView.setOnClickListener {
                selectVariant(
                    exerciseVariantsMissedOperatorBlocksLayoutVariantsLinearLayout,
                    idx
                )
                exercisesViewModel?.exerciseRequestData = ExerciseRequestData(variants.toList()[idx])
                if (isBonus)
                    exercisesViewModel?.checkBonusResult()
            }
        }
    }

}