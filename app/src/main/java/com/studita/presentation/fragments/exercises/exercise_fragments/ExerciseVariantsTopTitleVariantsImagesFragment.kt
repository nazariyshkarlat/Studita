package com.studita.presentation.fragments.exercises.exercise_fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.studita.R
import com.studita.domain.entity.exercise.ExerciseRequestData
import com.studita.presentation.model.ExerciseImagesRowUiModel
import com.studita.presentation.model.ExerciseUiModel
import com.studita.presentation.views.SquareView
import com.studita.utils.dpToPx
import com.studita.utils.makeView
import com.studita.utils.postExt
import com.google.android.flexbox.FlexboxLayout
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_title_layout.*


class ExerciseVariantsTopTitleVariantsImagesFragment :
    ExerciseVariantsFragment(R.layout.exercise_variants_title_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel?.let { vm ->
            when (vm.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType1UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType1UiModel
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

    private fun fillVariants(variants: List<ExerciseImagesRowUiModel>) {
        variants.forEach { variant ->
            val variantView: View
            if (variant.count == 0) {
                variantView =
                    exerciseVariantsTitleLayoutLinearLayout.makeView(R.layout.exercise_variant_text_item) as TextView
                variantView.exerciseVariantTextItem.text =
                    resources.getString(R.string.exercise_image_0_count)
            } else {
                variantView =
                    exerciseVariantsTitleLayoutLinearLayout.makeView(R.layout.exercise_variant_linear_item) as FlexboxLayout
                for (i in 0 until variant.count) {
                    val emojiView = SquareView(exerciseVariantsTitleLayoutLinearLayout.context)
                    val params = FlexboxLayout.LayoutParams(
                        FlexboxLayout.LayoutParams.WRAP_CONTENT,
                        FlexboxLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.height = 24F.dpToPx()
                    params.width = 24F.dpToPx()
                    emojiView.layoutParams = params
                    emojiView.background = variant.image
                    variantView.addView(emojiView)
                }
            }
            variantView.setOnClickListener {
                selectVariant(
                    exerciseVariantsTitleLayoutLinearLayout,
                    exerciseVariantsTitleLayoutLinearLayout.indexOfChild(it)
                )
                exercisesViewModel?.exerciseRequestData =
                    ExerciseRequestData(variant.count.toString())
            }
            exerciseVariantsTitleLayoutLinearLayout.addView(variantView)
        }
    }
}