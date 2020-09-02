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
import com.example.studita.domain.entity.exercise.ExerciseSymbolData
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.utils.makeView
import com.example.studita.utils.postExt
import kotlinx.android.synthetic.main.exercise_title_number_number_name_true_false.*
import kotlinx.android.synthetic.main.exercise_title_number_number_name_variants.*
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_long_title_true_false.*
import kotlinx.android.synthetic.main.exercise_variants_true_false.*

class ExerciseVariantsTrueFalseLongTitleFragment : ExerciseVariantsFragment(R.layout.exercise_variants_long_title_true_false) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel?.let { vm ->
            when (vm.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType22UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType22UiModel
                    exerciseVariantsLongTitleTrueFalseTitle.text = exerciseUiModel.title
                    fillVariants()
                }
            }

            observeAnswered(vm, exerciseVariantsLongTitleTrueFalseLinearLayout)
            observeBonus(exerciseVariantsLongTitleTrueFalseLinearLayout)
        }
        selectCurrentVariant(exerciseVariantsLongTitleTrueFalseLinearLayout)
    }

    private fun fillVariants() {
        (exerciseVariantsLongTitleTrueFalseTrue as TextView).text =
            resources.getString(R.string.true_variant)
        (exerciseVariantsLongTitleTrueFalseFalse as TextView).text =
            resources.getString(R.string.false_variant)
        for (variantView in exerciseVariantsLongTitleTrueFalseLinearLayout.children) {
            variantView.setOnClickListener {
                selectVariant(
                    exerciseVariantsLongTitleTrueFalseLinearLayout,
                    exerciseVariantsLongTitleTrueFalseLinearLayout.indexOfChild(it)
                )
                exercisesViewModel?.exerciseRequestData = ExerciseRequestData(
                    if (exerciseVariantsLongTitleTrueFalseLinearLayout.indexOfChild(
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