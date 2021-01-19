package com.studita.presentation.fragments.exercises.exercise_fragments

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.OneShotPreDrawListener
import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import com.studita.R
import com.studita.domain.entity.exercise.ExerciseRequestData
import com.studita.presentation.model.ExerciseUiModel
import com.studita.utils.*
import kotlinx.android.synthetic.main.exercise_variants_long_title_true_false.*
import kotlinx.android.synthetic.main.exercise_variants_missed_operator_blocks_layout.*
import kotlinx.android.synthetic.main.exercise_variants_title_layout.*

class ExerciseVariantsMissedOperatorBlocksFragment : ExerciseVariantsFragment(R.layout.exercise_variants_missed_operator_blocks_layout){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel?.let { vm ->
            when (vm.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType23UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType23UiModel
                    exerciseVariantsMissedOperatorBlocksLayoutTitleFirstPart.text = exerciseUiModel.titleParts.first
                    exerciseVariantsMissedOperatorBlocksLayoutTitleSecondPart.text = exerciseUiModel.titleParts.second
                    exerciseVariantsMissedOperatorBlocksLayoutSubtitle.text = exerciseUiModel.subtitle
                    fillVariants(exerciseUiModel.variants)
                }
            }
            observeAnswered(vm, exerciseVariantsMissedOperatorBlocksLayoutVariantsLinearLayout)
            observeBonus(exerciseVariantsMissedOperatorBlocksLayoutVariantsLinearLayout)
        }
        selectCurrentVariant(exerciseVariantsMissedOperatorBlocksLayoutVariantsLinearLayout)

        OneShotPreDrawListener.add(view){
            exerciseVariantsMissedOperatorBlocksLayoutVariantsLinearLayout.updateLayoutParams<FrameLayout.LayoutParams> {
                height = exerciseVariantsMissedOperatorBlocksLayoutVariantsLinearLayout.measuredHeight+
                        exerciseVariantsMissedOperatorBlocksLayoutTopLinearLayout.measuredHeight +
                        32F.dp
            }
        }
    }

    private fun fillVariants(variants: Pair<String, String>) {
        exerciseVariantsMissedOperatorBlocksLayoutLeftVariant.text = variants.first
        exerciseVariantsMissedOperatorBlocksLayoutRightVariant.text = variants.second
        exerciseVariantsMissedOperatorBlocksLayoutVariantsLinearLayout.children.forEachIndexed{ idx, variantView ->
            variantView.setChildOnTouch(idx, variants.toList()[idx])
        }
    }

    private fun View.setChildOnTouch(idx: Int, variant: String) {

        setOnTouchListener(object : View.OnTouchListener {

            private var dX: Float = 0F
            private var dY: Float = 0F

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        dX = v.x - event.rawX
                        dY = v.y - event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {

                        val newX = event.rawX + dX

                        if (exerciseVariantsMissedOperatorBlocksLayoutVariantsLinearLayout.childIsInParentX(
                                newX.toInt(),
                                (newX + v.measuredWidth).toInt()
                            )
                        ) {
                            v.x = newX
                        } else {
                            if (newX < 0F) {
                                v.x = 0F
                            } else {
                                v.x =
                                    (exerciseVariantsMissedOperatorBlocksLayoutVariantsLinearLayout.measuredWidth - v.measuredWidth).toFloat()
                            }
                        }
                        val newY = event.rawY + dY
                        if (exerciseVariantsMissedOperatorBlocksLayoutVariantsLinearLayout.childIsInParentY(
                                newY.toInt(),
                                (newY + v.measuredHeight).toInt()
                            )
                        ) {
                            v.y = newY
                        } else {
                            if (newY < 0F) {
                                v.y = 0F
                            } else {
                                v.y =
                                    (exerciseVariantsMissedOperatorBlocksLayoutVariantsLinearLayout.measuredHeight - v.measuredHeight).toFloat()
                            }
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        when {
                            v.isClick(event) -> {
                                v.onSelect(idx, variant)
                            }
                            v.isContainsAnotherView(exerciseVariantsMissedOperatorBlocksLayoutEmptyBlock) -> {
                                v.onSelect(idx, variant)
                            }
                            else -> {
                                v.animate().translationX(0F).translationY(0F).start()
                            }
                        }
                        return true
                    }
                    else -> return false
                }
            }

        })
    }

    private fun View.onSelect(idx: Int, variant: String){
        selectVariant(
            exerciseVariantsMissedOperatorBlocksLayoutVariantsLinearLayout,
            idx
        )
        animate()
            .x(exerciseVariantsMissedOperatorBlocksLayoutEmptyBlock.x)
            .y(exerciseVariantsMissedOperatorBlocksLayoutEmptyBlock.y)
            .setDuration(200L)
            .start()
        exercisesViewModel?.exerciseRequestData = ExerciseRequestData(variant)
        if (isBonus)
            exercisesViewModel?.checkBonusResult()
    }

}