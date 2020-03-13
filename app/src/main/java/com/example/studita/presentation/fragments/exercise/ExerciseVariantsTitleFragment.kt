package com.example.studita.presentation.fragments.exercise

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
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
import com.example.studita.presentation.views.SquareView
import com.google.android.flexbox.FlexboxLayout
import kotlinx.android.synthetic.main.exercise_variant_text_item.view.*
import kotlinx.android.synthetic.main.exercise_variants_title_fragment.*


class ExerciseVariantsTitleFragment : NavigatableFragment(R.layout.exercise_variants_title_fragment) {

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
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType1UiModel -> {
                    val exerciseUiModel = it.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType1UiModel
                    exerciseVariantsTitleFragmentTitle.text = exerciseUiModel.title
                    exerciseVariantsTitleFragmentSubtitle.text = exerciseUiModel.subtitle
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

    private fun fillLinearLayout(variants: List<ExerciseShape>){
        for(variant in variants) {
            val variantView: View
            if(variant.count == 0){
                variantView = exerciseVariantsTitleFragmentLinearLayout.makeView(R.layout.exercise_variant_text_item) as TextView
                variantView.exerciseVariantFlexboxItem.text = resources.getString(R.string.exercise_shape_0_rect)
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
                    shapeView.background = ContextCompat.getDrawable(
                        exerciseVariantsTitleFragmentLinearLayout.context,
                        R.drawable.exercise_rectangle
                    )
                    variantView.addView(shapeView)
                }
            }
            variantView.setOnClickListener {
                exercisesViewModel?.selectedPos = exerciseVariantsTitleFragmentLinearLayout.indexOfChild(it)
                exercisesViewModel?.selectedPos?.let { it1 -> selectVariant(it1) }
                exercisesViewModel?.exerciseRequestData = ExerciseRequestData(variant.count.toString())
            }
            exerciseVariantsTitleFragmentLinearLayout.addView(variantView)
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