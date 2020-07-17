package com.example.studita.presentation.fragments.exercises.exercise

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.utils.dpToPx
import com.example.studita.utils.makeView
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.views.SquareView
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_linear_fragment.*


class ExerciseVariantsType2Fragment : ExerciseVariantsFragment(R.layout.exercise_variants_linear_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel?.let {
            observeAnswered(it, exerciseVariantsLinearFragmentCenterLinearLayout)
            when (it.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType2And14UiModel -> {
                    val exerciseUiModel =
                        it.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType2And14UiModel
                    fillLinearLayout(exerciseUiModel)
                    exerciseVariantsLinearFragmentSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                }
            }
            if (selectedPos != -1)
                selectVariant(exerciseVariantsLinearFragmentCenterLinearLayout, selectedPos)
        }

    }

    private fun fillVariants(variants: List<String>){
        variants.forEach { variant ->
            val variantView = exerciseVariantsLinearFragmentCenterLinearLayout.makeView(R.layout.exercise_variant_text_item)
            variantView.exerciseVariantTextItem.text = variant
            variantView.setOnClickListener {
                selectVariant(
                    exerciseVariantsLinearFragmentCenterLinearLayout,
                    exerciseVariantsLinearFragmentCenterLinearLayout.indexOfChild(it)
                )
                exercisesViewModel?.exerciseRequestData = ExerciseRequestData(variant)
            }
            exerciseVariantsLinearFragmentCenterLinearLayout.addView(variantView)
        }
    }

    private fun fillLinearLayout(exerciseUiModel: ExerciseUiModel.ExerciseUiModelExercise.ExerciseType2And14UiModel){
        (0 until exerciseUiModel.title.count).forEach { _ ->
            val shapeView = SquareView(exerciseVariantsLinearFragmentTopFlexboxLayout.context)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.height = 32.dpToPx()
            params.width = 32.dpToPx()
            shapeView.layoutParams = params
            shapeView.background = ContextCompat.getDrawable(
                exerciseVariantsLinearFragmentTopFlexboxLayout.context,
                R.drawable.exercise_rectangle_green
            )
            exerciseVariantsLinearFragmentTopFlexboxLayout.addView(shapeView)
        }
    }
}