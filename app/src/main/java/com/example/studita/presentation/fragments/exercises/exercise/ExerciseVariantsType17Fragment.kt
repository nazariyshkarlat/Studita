package com.example.studita.presentation.fragments.exercises.exercise

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.presentation.model.ExerciseOperatorUiModel
import com.example.studita.presentation.model.ExerciseShapeUiModel
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.views.SquareView
import com.example.studita.utils.dpToPx
import com.example.studita.utils.makeView
import com.example.studita.utils.postExt
import com.google.android.flexbox.FlexboxLayout
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_linear_fragment.*

class ExerciseVariantsType17Fragment : ExerciseVariantsFragment(R.layout.exercise_variants_linear_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel?.let { vm ->
            observeAnswered(vm, exerciseVariantsLinearFragmentCenterLinearLayout)
            when (vm.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType17UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType17UiModel
                    exerciseVariantsLinearFragmentSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                    fillLinearLayout(exerciseUiModel)
                }
            }

            if (selectedPos != -1)
                exerciseVariantsLinearFragmentCenterLinearLayout.postExt {
                    it as ViewGroup
                    selectVariant(it, selectedPos)
                }
            observeAnswered(vm, exerciseVariantsLinearFragmentCenterLinearLayout)
        }
    }

    private fun fillVariants(variants: List<ExerciseShapeUiModel>){
        variants.forEach { variant ->
            val variantView: View
            if(variant.count == 0){
                variantView = exerciseVariantsLinearFragmentCenterLinearLayout.makeView(R.layout.exercise_variant_text_item) as TextView
                variantView.exerciseVariantTextItem.text = resources.getString(R.string.exercise_shape_0_rect)
            }else {
                variantView = exerciseVariantsLinearFragmentCenterLinearLayout.makeView(R.layout.exercise_variant_linear_item) as FlexboxLayout
                for (i in 0 until variant.count) {
                    val shapeView = SquareView(exerciseVariantsLinearFragmentCenterLinearLayout.context)
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
                selectVariant(exerciseVariantsLinearFragmentCenterLinearLayout, exerciseVariantsLinearFragmentCenterLinearLayout.indexOfChild(it))
                exercisesViewModel?.exerciseRequestData = ExerciseRequestData(variant.count.toString())
            }
            exerciseVariantsLinearFragmentCenterLinearLayout.addView(variantView)
        }
    }

    private fun fillLinearLayout(exerciseUiModel: ExerciseUiModel.ExerciseUiModelExercise.ExerciseType17UiModel){
        (exerciseUiModel.exerciseShapeEquationUiModel).forEach {
            if(it is ExerciseShapeUiModel) {
                (0 until it.count).forEach { _ ->
                    val shapeView =
                        SquareView(exerciseVariantsLinearFragmentTopFlexboxLayout.context)
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
            }else{
                val operatorView = TextView(exerciseVariantsLinearFragmentTopFlexboxLayout.context)
                TextViewCompat.setTextAppearance(operatorView, R.style.Medium24)
                with(operatorView) {
                    val params = ViewGroup.MarginLayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    params.marginStart = (-4).dpToPx()
                    params.marginEnd = (-4).dpToPx()
                    layoutParams = params
                    text = (it as ExerciseOperatorUiModel).operator.toString()
                }
                exerciseVariantsLinearFragmentTopFlexboxLayout.addView(operatorView)
            }
        }
    }
}