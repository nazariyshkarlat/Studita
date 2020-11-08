package com.studita.presentation.fragments.exercises.exercise_fragments

import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.studita.R
import com.studita.domain.entity.exercise.ExerciseRequestData
import com.studita.presentation.fragments.base.NavigatableFragment
import com.studita.presentation.model.ExerciseUiModel
import com.studita.presentation.view_model.ExercisesViewModel
import com.studita.utils.setAllClickable
import kotlinx.android.synthetic.main.exercise_input_equation_layout.*


class ExerciseInputEquationFragment : NavigatableFragment(R.layout.exercise_input_equation_layout),
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
                { answered ->
                    if (answered) {
                        exerciseInputEquationLayoutKeyboard.setAllClickable(false)
                        exerciseInputEquationLayoutEditText.isFocusable = false
                    }
                })
            if (it.exerciseUiModel is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType9UiModel) {
                val exerciseUiModel =
                    it.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType9UiModel
                exerciseInputEquationLayoutResultTextView.text = exerciseUiModel.title
                exerciseInputEquationLayoutBottomTextView.text = exerciseUiModel.subtitle
            }
        }
        exerciseInputEquationLayoutEditText.addTextChangedListener(this)
        exerciseInputEquationLayoutKeyboard.syncWithTextView(exerciseInputEquationLayoutEditText)
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        var str = s.toString()
        if (str.isNotEmpty()) {
            if ((str[0].isCharacter()) or (str[0] == '0')) {
                exerciseInputEquationLayoutEditText.text.delete(0, 1)
                return
            }
            if (str.length >= 2) {
                val removeDuplicate = str.removeCharDuplicate()
                val removeZeros = removeDuplicate.removeZeros()
                if (str != removeZeros)
                    exerciseInputEquationLayoutEditText.setText(removeZeros)
                str = removeZeros
                val lastChar = str[str.lastIndex]
                if (!lastChar.isCharacter()) {
                    if (str.checkStringIsValidEquation()) {
                        exercisesViewModel?.buttonEnabledState?.value =
                            true
                        exercisesViewModel?.exerciseRequestData =
                            ExerciseRequestData(str)
                    } else {
                        exercisesViewModel?.buttonEnabledState?.value =
                            false
                    }
                } else
                    exercisesViewModel?.buttonEnabledState?.value =
                        false
                return
            }
        }else{
            exercisesViewModel?.buttonEnabledState?.value =
                false
        }
    }

    private fun String.checkStringIsValidEquation(): Boolean {
        return (this.length >= 3) and ((this.contains('+')) or (this.contains('-')) or (this.contains(
            '*'
        )) or (this.contains('/')) or (this.contains(':')) or (this.contains('×')) or (this.contains(
            '÷'
        )))
    }

    private fun Char.isCharacter(): Boolean {
        return (this == '+') or (this == '-') or (this == '*') or (this == '/') or (this == ':') or (this == '×') or (this == '÷')
    }

    private fun String.removeCharDuplicate(): String {
        var result = this
        this.forEachIndexed { index, c ->
            if (c.isCharacter() and (index != 0))
                if (this[index - 1].isCharacter()) {
                    result =
                        (this.substring(0, index) + this.substring(index + 1)).removeCharDuplicate()
                }
        }
        return result
    }

    private fun String.removeZeros(): String {
        var result = this
        this.forEachIndexed { index, c ->
            if ((c == '0') and (index != 0))
                if (this[index - 1].isCharacter()) {
                    result =
                        (this.substring(0, index) + this.substring(index + 1)).removeCharDuplicate()
                }
        }
        return result
    }
}