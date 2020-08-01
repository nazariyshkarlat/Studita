package com.example.studita.presentation.fragments.exercises.exercise

import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.lifecycle.Observer
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseData
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.presentation.model.ExerciseImagesRowUiModel
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.views.SquareView
import com.example.studita.utils.dpToPx
import com.example.studita.utils.postExt
import kotlinx.android.synthetic.main.exercise_variants_linear_fragment.*
import kotlinx.android.synthetic.main.exercise_variants_title_with_images_true_false.*
import kotlinx.android.synthetic.main.exercise_variants_true_false.*

class ExerciseVariantsType18Fragment :  ExerciseVariantsFragment(R.layout.exercise_variants_title_with_images_true_false){

    var isBonus = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel?.let {vm->
            when (vm.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType18UiModel -> {
                    val exerciseUiModel =
                        vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType18UiModel
                    exerciseVariantsTitleWithImagesTrueFalseTitle.text = exerciseUiModel.title
                    fillVariants()
                    fillLinearLayout(exerciseUiModel.titleImages)
                }
            }

            isBonus = (exercisesViewModel?.exerciseData as? ExerciseData.ExerciseDataExercise)?.isBonus == true

            observeAnswered(vm, exerciseVariantsTitleWithImagesTrueFalseLinearLayout)

            if(!vm.isBonusCompleted){
                vm.exerciseBonusResultState.observe(viewLifecycleOwner, Observer {answerIsCorrect->
                    if(answerIsCorrect != null){
                    with(getSelectedChild(exerciseVariantsTitleWithImagesTrueFalseLinearLayout) as TextView) {
                        isActivated = answerIsCorrect
                        (background as TransitionDrawable).startTransition(
                            resources.getInteger(R.integer.button_transition_duration)
                        )
                        setTextColor(ContextCompat.getColor(view.context, R.color.white))
                    }
                }})
            }
        }

        if (selectedPos != -1) {
            if(!isBonus)
                exerciseVariantsTitleWithImagesTrueFalseLinearLayout.postExt {
                    it as ViewGroup
                    selectVariant(it, selectedPos)
                }
        }
    }

    private fun fillVariants(){
        (exerciseVariantsTitleWithImagesTrueFalseTrue as TextView).text = resources.getString(R.string.true_variant)
        (exerciseVariantsTitleWithImagesTrueFalseFalse as TextView).text = resources.getString(R.string.false_variant)
        for(variantView in exerciseVariantsTitleWithImagesTrueFalseLinearLayout.children) {
            variantView.setOnClickListener {
                selectVariant(
                    exerciseVariantsTitleWithImagesTrueFalseLinearLayout,
                    exerciseVariantsTitleWithImagesTrueFalseLinearLayout.indexOfChild(it)
                )
                exercisesViewModel?.exerciseRequestData= ExerciseRequestData(
                    if (exerciseVariantsTitleWithImagesTrueFalseLinearLayout.indexOfChild(variantView) == 0) "true" else "false"
                )
                if(isBonus)
                    exercisesViewModel?.checkBonusResult()
            }
        }
    }

    private fun fillLinearLayout(exerciseImagesRowUiModel: ExerciseImagesRowUiModel){
        (0 until exerciseImagesRowUiModel.count).forEach { _ ->
            val emojiView = SquareView(exerciseVariantsTitleWithImagesTrueFalseFlexboxLayout.context)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.height = 32.dpToPx()
            params.width = 32.dpToPx()
            emojiView.layoutParams = params
            emojiView.background = exerciseImagesRowUiModel.image
            exerciseVariantsTitleWithImagesTrueFalseFlexboxLayout.addView(emojiView)
        }
    }

}