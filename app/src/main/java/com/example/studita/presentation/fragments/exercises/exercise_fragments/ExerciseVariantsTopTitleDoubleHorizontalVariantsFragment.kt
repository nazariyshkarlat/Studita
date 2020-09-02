package com.example.studita.presentation.fragments.exercises.exercise_fragments

import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseData
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.domain.entity.exercise.ExerciseVariantData
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.utils.makeView
import com.example.studita.utils.postExt
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_title_layout.*

class ExerciseVariantsTopTitleDoubleHorizontalVariantsFragment :
    ExerciseVariantsFragment(R.layout.exercise_variants_title_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel?.let { vm ->
            when (vm.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType8UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType8UiModel
                    exerciseVariantsTitleLayoutTitle.text = exerciseUiModel.title
                    exerciseVariantsTitleLayoutSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                }
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType12UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType12UiModel
                    exerciseVariantsTitleLayoutTitle.text = exerciseUiModel.title
                    exerciseVariantsTitleLayoutSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                }
            }

            observeAnswered(vm, exerciseVariantsTitleLayoutLinearLayout)
            observeBonus(exerciseVariantsTitleLayoutLinearLayout)

        }

        selectCurrentVariant(exerciseVariantsTitleLayoutLinearLayout)

    }

    private fun fillVariants(variants: List<ExerciseVariantData>) {
        var childLinearLayout = LinearLayout(context)
        variants.forEachIndexed { idx, variant ->
            if (idx % 2 == 0) {
                if (idx != 0)
                    childLinearLayout = LinearLayout(context)
                childLinearLayout.orientation = LinearLayout.HORIZONTAL
                exerciseVariantsTitleLayoutLinearLayout.addView(childLinearLayout)
            }
            val variantView =
                exerciseVariantsTitleLayoutLinearLayout.makeView(R.layout.exercise_variant_text_item)
            variantView.exerciseVariantTextItem.text = variant.variantText
            variantView.setOnClickListener {
                selectVariant(
                    exerciseVariantsTitleLayoutLinearLayout,
                    idx
                )
                exercisesViewModel?.exerciseRequestData = ExerciseRequestData(variant.meta ?: variant.variantText)
                if (isBonus)
                    exercisesViewModel?.checkBonusResult()
            }
            childLinearLayout.addView(variantView)
        }
    }

}