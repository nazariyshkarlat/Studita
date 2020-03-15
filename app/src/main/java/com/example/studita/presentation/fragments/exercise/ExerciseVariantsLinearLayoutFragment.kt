package com.example.studita.presentation.fragments.exercise

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.OneShotPreDrawListener
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.presentation.extensions.dpToPx
import com.example.studita.presentation.extensions.makeView
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_linear_fragment.*


class ExerciseVariantsLinearLayoutFragment : ExerciseVariantsFragment(R.layout.exercise_variants_linear_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel?.let {
            observeAnswered(it, exerciseVariantsLinearFragmentCenterLinearLayout)
            when (it.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType2UiModel -> {
                    val exerciseUiModel =
                        it.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType2UiModel
                    fillLinearLayout(exerciseUiModel)
                    exerciseVariantsLinearFragmentSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                }
            }
            if (it.selectedPos != -1)
                selectVariant(exerciseVariantsLinearFragmentCenterLinearLayout, it.selectedPos)
        }

        onPreDraw(exerciseVariantsLinearFragmentScrollView)
    }

    private fun fillVariants(variants: List<String>){
        for(variant in variants) {
            val variantView = exerciseVariantsLinearFragmentCenterLinearLayout.makeView(R.layout.exercise_variant_text_item)
            variantView.exerciseVariantTextItem.text = variant
            variantView.setOnClickListener {
                exercisesViewModel?.selectedPos = exerciseVariantsLinearFragmentCenterLinearLayout.indexOfChild(it)
                exercisesViewModel?.selectedPos?.let { it1 -> selectVariant(exerciseVariantsLinearFragmentCenterLinearLayout, it1) }
                exercisesViewModel?.exerciseRequestData = ExerciseRequestData(variant)
            }
            exerciseVariantsLinearFragmentCenterLinearLayout.addView(variantView)
        }
    }

    private fun fillLinearLayout(exerciseUiModel: ExerciseUiModel.ExerciseUiModelExercise.ExerciseType2UiModel){
        for (i in 0 until exerciseUiModel.title.count) {
            val shapeView = View(exerciseVariantsLinearFragmentTopLinearLayout.context)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.height = 32.dpToPx()
            params.width = 32.dpToPx()
            params.leftMargin = 8.dpToPx()
            params.rightMargin = 8.dpToPx()
            shapeView.layoutParams = params
            shapeView.background = ContextCompat.getDrawable(
                exerciseVariantsLinearFragmentTopLinearLayout.context,
                R.drawable.exercise_rectangle
            )
            exerciseVariantsLinearFragmentTopLinearLayout.addView(shapeView)
        }
    }
}