package com.example.studita.presentation.fragments.exercises.exercise

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.presentation.model.ExerciseImagesRowUiModel
import com.example.studita.utils.dpToPx
import com.example.studita.utils.makeView
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.views.SquareView
import com.example.studita.utils.postExt
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_linear_fragment.*
import kotlinx.android.synthetic.main.exercise_variants_title_fragment.*


class ExerciseVariantsType2Fragment : ExerciseVariantsFragment(R.layout.exercise_variants_linear_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel?.let {vm->
            when (vm.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType2UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType2UiModel
                    fillLinearLayout(exerciseUiModel.title)
                    exerciseVariantsLinearFragmentSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
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

    private fun fillLinearLayout(exerciseImagesRowUiModel: ExerciseImagesRowUiModel){
        (0 until exerciseImagesRowUiModel.count).forEach { _ ->
            val emojiView = SquareView(exerciseVariantsLinearFragmentTopFlexboxLayout.context)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.height = 32.dpToPx()
            params.width = 32.dpToPx()
            emojiView.layoutParams = params
            emojiView.background = exerciseImagesRowUiModel.image
            exerciseVariantsLinearFragmentTopFlexboxLayout.addView(emojiView)
        }
    }
}