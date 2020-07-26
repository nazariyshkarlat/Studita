package com.example.studita.presentation.fragments.exercises.exercise

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.utils.makeView
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.utils.postExt
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_linear_fragment.*
import kotlinx.android.synthetic.main.exercise_variants_title_fragment.*

class ExerciseVariantsType8Fragment : ExerciseVariantsFragment(R.layout.exercise_variants_title_fragment){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel?.let {vm->
            observeAnswered(vm, exerciseVariantsTitleFragmentLinearLayout)
            when (vm.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType8And12UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType8And12UiModel
                    exerciseVariantsTitleFragmentTitle.text = exerciseUiModel.title
                    exerciseVariantsTitleFragmentSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                }
            }
            if (selectedPos != -1)
                exerciseVariantsTitleFragmentLinearLayout.postExt {
                    it as ViewGroup
                    selectVariant(it, selectedPos)
                }
            observeAnswered(vm, exerciseVariantsLinearFragmentCenterLinearLayout)
        }
    }

    private fun fillVariants(variants: List<String>){
        var childLinearLayout = LinearLayout(context)
        variants.forEachIndexed {idx,  variant ->
            if(idx%2 == 0){
                if(idx != 0)
                    childLinearLayout = LinearLayout(context)
                childLinearLayout.orientation = LinearLayout.HORIZONTAL
                exerciseVariantsTitleFragmentLinearLayout.addView(childLinearLayout)
            }
            val variantView = exerciseVariantsTitleFragmentLinearLayout.makeView(R.layout.exercise_variant_text_item)
            variantView.exerciseVariantTextItem.text = variant
            variantView.setOnClickListener {
                selectVariant(
                    exerciseVariantsTitleFragmentLinearLayout,
                    idx
                )
                exercisesViewModel?.exerciseRequestData = ExerciseRequestData(variant)
            }
            childLinearLayout.addView(variantView)
        }
    }

}