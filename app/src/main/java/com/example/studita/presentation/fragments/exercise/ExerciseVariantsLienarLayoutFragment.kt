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
import com.example.studita.presentation.model.ExerciseShape
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_linear_fragment.*
import kotlinx.android.synthetic.main.exercise_variants_title_fragment.*


class ExerciseVariantsLienarLayoutFragment : NavigatableFragment(R.layout.exercise_variants_linear_fragment) {

    private var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesViewModel?.let {
            it.answered.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer { answered ->
                    if (answered)
                        exerciseVariantsTitleFragmentLinearLayout.disableAllItems()
                })

            when (it.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType2UiModel -> {
                    val exerciseUiModel = it.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType2UiModel
                    for(i in 0 until exerciseUiModel.title.count) {
                        val shapeView = View(exerciseVariantsTitleFragmentLinearLayout.context)
                        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        params.height = 32.dpToPx()
                        params.width = 32.dpToPx()
                        params.leftMargin = 8.dpToPx()
                        params.rightMargin = 8.dpToPx()
                        shapeView.layoutParams = params
                        shapeView.background =  ContextCompat.getDrawable(exerciseVariantsTitleFragmentLinearLayout.context, R.drawable.exercise_rectangle)
                        exerciseVariantsLinearFragmentTopLinearLayout.addView(shapeView)
                    }
                    fillLinearLayout(exerciseUiModel.variants)
                }
            }

            if(it.selectedPos != -1)
                selectVariant(it.selectedPos)
        }

        OneShotPreDrawListener.add(exerciseVariantsTitleFragmentScrollView){
            if (exerciseVariantsTitleFragmentScrollView.height < exerciseVariantsTitleFragmentScrollView.getChildAt(
                    0
                ).height + exerciseVariantsTitleFragmentScrollView.paddingTop + exerciseVariantsTitleFragmentScrollView.paddingBottom) {
                exerciseVariantsTitleFragmentScrollView.background =
                    context?.getDrawable(R.drawable.divider_top_bottom_drawable)
            }
        }

    }

    private fun View.refreshVariants() {
        this.isEnabled = true
        this.isSelected = false
        if (this is ViewGroup) {
            for (i in 0 until this.childCount) {
                val child = this.getChildAt(i)
                child.refreshVariants()
            }
        }
    }

    private fun View.disableAllItems(){
        this.isEnabled = false
        if (this is ViewGroup) {
            for (i in 0 until this.childCount) {
                val child = this.getChildAt(i)
                child.disableAllItems()
            }
        }
    }

    private fun fillLinearLayout(variants: List<String>){
        for(variant in variants) {
            val variantView = exerciseVariantsLinearFragmentCenterLinearLayout.makeView(R.layout.exercise_variant_text_item)
            variantView.exerciseVariantTextItem.text = variant
            variantView.setOnClickListener {
                exercisesViewModel?.selectedPos = exerciseVariantsLinearFragmentCenterLinearLayout.indexOfChild(it)
                exercisesViewModel?.selectedPos?.let { it1 -> selectVariant(it1) }
                ExerciseRequestData(variant)
            }
            exerciseVariantsLinearFragmentCenterLinearLayout.addView(variantView)
        }
    }

    private fun selectVariant(position: Int){
        val childView = exerciseVariantsTitleFragmentLinearLayout.getChildAt(position)
        exercisesViewModel?.setButtonEnabled(true)
        exerciseVariantsTitleFragmentLinearLayout.refreshVariants()
        childView.isEnabled = false
        childView.isSelected = true
    }
}