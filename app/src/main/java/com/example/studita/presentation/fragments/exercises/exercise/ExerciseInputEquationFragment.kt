package com.example.studita.presentation.fragments.exercises.exercise

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.utils.hideKeyboard
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercise_input_equation_layout.*

class ExerciseInputEquationFragment : NavigatableFragment(R.layout.exercise_input_equation_layout), TextWatcher {

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
                        (activity as AppCompatActivity).hideKeyboard()
                        exerciseInputEquationLayoutEditText.isFocusable = false
                    }
                })
            if (it.exerciseUiModel is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType9UiModel) {
                val exerciseUiModel =
                    it.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType9UiModel
                exerciseInputEquationLayoutResultTextView.text = exerciseUiModel.title
                exerciseInputEquationLayoutBottomTextView.text =
                    resources.getString(R.string.exercise_type_9_bottom_text)
            }
        }
        exerciseInputEquationLayoutEditText.addTextChangedListener(this)
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        var str = s.toString()
        if(str.isNotEmpty()) {
            val lastChar = str[str.lastIndex]
            if ((str[0].isCharacter()) or (str[0] == '0'))
                exerciseInputEquationLayoutEditText.setText(
                    exerciseInputEquationLayoutEditText.text.substring(
                        1,
                        exerciseInputEquationLayoutEditText.text.length
                    )
                )
            if (str.length >= 2) {
                val removeDuplicate = str.removeCharDuplicate()
                val removeZeros = removeDuplicate.removeZeros()
                if (str != removeZeros)
                    exerciseInputEquationLayoutEditText.setText(removeZeros)
                exerciseInputEquationLayoutEditText.setSelection(
                    exerciseInputEquationLayoutEditText.text.length
                )
                str = removeZeros
                if (!lastChar.isCharacter()) {
                    if (str.checkStringIsValidEquation()) {
                        println(str)
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
            }
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
            if (c.isCharacter() and (index !=0))
                if (this[index - 1].isCharacter()) {
                    result = (this.substring(0, index) + this.substring(index + 1)).removeCharDuplicate()
                }
        }
        return result
    }

    private fun String.removeZeros() : String{
        var result = this
        this.forEachIndexed { index, c ->
            if ((c == '0') and (index !=0))
                if (this[index - 1].isCharacter()) {
                    result = (this.substring(0, index) + this.substring(index + 1)).removeCharDuplicate()
                }
        }
        return result
    }
}