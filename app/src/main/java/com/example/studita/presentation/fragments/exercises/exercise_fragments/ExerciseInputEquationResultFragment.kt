package com.example.studita.presentation.fragments.exercises.exercise_fragments

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import com.example.studita.utils.ThemeUtils
import com.example.studita.utils.setAllClickable
import kotlinx.android.synthetic.main.exercise_input_equation_layout.*
import kotlinx.android.synthetic.main.exercise_input_equation_result_layout.*


class ExerciseInputEquationResultFragment : NavigatableFragment(R.layout.exercise_input_equation_result_layout),
    TextWatcher {

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
                    if (answered) {
                        exerciseInputEquationResultLayoutKeyboard.setAllClickable(false)
                        exerciseInputEquationResultLayoutResultEditText.isFocusable = false
                    }
                })
            if (!it.isBonusCompleted) {
                it.exerciseBonusResultState.observe(
                    viewLifecycleOwner,
                    Observer { answerIsCorrect ->
                        if (answerIsCorrect != null) {
                            animateBonusResultTextColor(view.context, answerIsCorrect)
                        }
                    })
            }
            it.exerciseBonusResultState.observe(viewLifecycleOwner, Observer {  })
            if (it.exerciseUiModel is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType26UiModel) {
                val exerciseUiModel =
                    it.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType26UiModel
                exerciseInputEquationResultLayoutEquationTextView.text = exerciseUiModel.title
                exerciseInputEquationResultLayoutTopTextView.text = exerciseUiModel.subtitle
            }
        }
        exerciseInputEquationResultLayoutResultEditText.addTextChangedListener(this)
        exerciseInputEquationResultLayoutKeyboard.syncWithTextView(exerciseInputEquationResultLayoutResultEditText)

        exerciseInputEquationResultLayoutButton.setOnClickListener {
            exercisesViewModel?.checkBonusResult()
            exerciseInputEquationResultLayoutButton.setOnClickListener {}
        }

        exerciseInputEquationResultLayoutButton.isEnabled = exerciseInputEquationResultLayoutResultEditText.text.isNotEmpty()
    }

    override fun afterTextChanged(s: Editable?) { }
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val str = s.toString()
        if(str.isNotEmpty()) {
            if (str[0] == '0') {
                exerciseInputEquationResultLayoutResultEditText.text.delete(0, 1)
                return
            }
            exercisesViewModel?.exerciseRequestData =
                ExerciseRequestData(str)
        }
        exerciseInputEquationResultLayoutButton.isEnabled = str.isNotEmpty()
    }

    private fun animateBonusResultTextColor(context: Context, answerIsCorrect: Boolean) {
        with(ValueAnimator.ofObject(
            ArgbEvaluator(),
            exerciseInputEquationResultLayoutResultEditText.currentTextColor,
            if (answerIsCorrect) ThemeUtils.getGreenColor(context) else ThemeUtils.getRedColor(context)
        ) ){
            addUpdateListener { animator ->
                exerciseInputEquationResultLayoutResultEditText.setTextColor(
                    animator.animatedValue as Int
                )
            }
            interpolator = FastOutSlowInInterpolator()
            duration = resources.getInteger(R.integer.bonus_text_color_change_duration).toLong()
            start()
        }
    }

}