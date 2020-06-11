package com.example.studita.presentation.fragments.exercises.exercise

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.utils.dpToPx
import com.example.studita.utils.makeView
import com.example.studita.presentation.model.ExerciseShapeUiModel
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.views.SquareView
import com.google.android.flexbox.FlexboxLayout
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_title_fragment.*


class ExerciseVariantsType1Fragment : ExerciseVariantsFragment(R.layout.exercise_variants_title_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel?.let {
            observeAnswered(it, exerciseVariantsTitleFragmentLinearLayout)
            when (it.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType1UiModel -> {
                    val exerciseUiModel = it.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType1UiModel
                    exerciseVariantsTitleFragmentTitle.text = exerciseUiModel.title
                    exerciseVariantsTitleFragmentSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                }
            }

            if(it.selectedPos != -1)
                selectVariant(exerciseVariantsTitleFragmentLinearLayout, it.selectedPos)
        }
    }

    private fun fillVariants(variants: List<ExerciseShapeUiModel>){
        for(variant in variants) {
            val variantView: View
            if(variant.count == 0){
                variantView = exerciseVariantsTitleFragmentLinearLayout.makeView(R.layout.exercise_variant_text_item) as TextView
                variantView.exerciseVariantTextItem.text = resources.getString(R.string.exercise_shape_0_rect)
            }else {
                variantView = exerciseVariantsTitleFragmentLinearLayout.makeView(R.layout.exercise_variant_linear_item) as FlexboxLayout
                for (i in 0 until variant.count) {
                    val shapeView = SquareView(exerciseVariantsTitleFragmentLinearLayout.context)
                    val params = FlexboxLayout.LayoutParams(
                        FlexboxLayout.LayoutParams.WRAP_CONTENT,
                        FlexboxLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.height = 20.dpToPx()
                    params.width = 20.dpToPx()
                    shapeView.layoutParams = params
                    shapeView.background = variant.shape
                    variantView.addView(shapeView)
                }
            }
            variantView.setOnClickListener {
                exercisesViewModel?.selectedPos = exerciseVariantsTitleFragmentLinearLayout.indexOfChild(it)
                exercisesViewModel?.selectedPos?.let { it1 -> selectVariant(exerciseVariantsTitleFragmentLinearLayout, it1) }
                exercisesViewModel?.exerciseRequestData = ExerciseRequestData(variant.count.toString())
            }
            exerciseVariantsTitleFragmentLinearLayout.addView(variantView)
        }
    }
}