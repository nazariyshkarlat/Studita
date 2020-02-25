package com.example.studita.presentation.fragments.exercise

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.presentation.extensions.hideKeyboard
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercise_input_equation_fragment.*
import kotlinx.android.synthetic.main.exercise_input_fragment.*

class ExerciseInputEquationFrament : BaseFragment(R.layout.exercise_input_equation_fragment), TextWatcher {

    private var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesViewModel?.let {
            it.answered.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer{ answered ->
                    if(answered) {
                        (activity as AppCompatActivity).hideKeyboard()
                        exerciseInputEquationFragmentEditText.isFocusable = false
                    }
                })
        }

        val exerciseUiModel = arguments?.getParcelable<ExerciseUiModel>("EXERCISE")
        if (exerciseUiModel is ExerciseUiModel.ExerciseUi6) {
            exerciseInputEquationFragmentResultTextView.text = resources.getString(R.string.exercise_type_6_result, exerciseUiModel.result)
            exerciseInputEquationFragmentBottomTextView.text = resources.getString(R.string.exercise_type_6_bottom_text)
        }
        exerciseInputEquationFragmentEditText.addTextChangedListener(this)
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val str = s.toString()
        if(str.isNotEmpty()) {
            val lastChar = str[str.lastIndex]
            if (str[0].isCharacter())
                exerciseInputEquationFragmentEditText.setText( exerciseInputEquationFragmentEditText.text.substring(
                    1,
                    exerciseInputEquationFragmentEditText.text.length
                ))
            if (str.length >= 2) {
                if ((str[str.lastIndex - 1] == lastChar) and (lastChar.isCharacter())) {
                    exerciseInputEquationFragmentEditText.setText(
                        str.substring(
                            0,
                            str.length - 1
                        )
                    )
                    exerciseInputEquationFragmentEditText.setSelection(
                        exerciseInputEquationFragmentEditText.text.length
                    )
                }else {
                    if (!lastChar.isCharacter()) {
                        if(str.checkStringIsValidEquation()) {
                            exercisesViewModel?.exercisesButtonState?.value =
                                true
                            exercisesViewModel?.exerciseRequestData =
                                ExerciseRequestData(str)
                        }else{
                            exercisesViewModel?.exercisesButtonState?.value =
                                false
                        }
                    }else
                        exercisesViewModel?.exercisesButtonState?.value =
                            false
                }
            }
        }
    }

    private fun String.checkStringIsValidEquation(): Boolean{
        return (this.length >= 3) and ((this.contains('+')) or (this.contains('-')) or (this.contains('*')) or (this.contains('/')) or (this.contains(':')) or (this.contains('×')) or (this.contains('÷')))
    }

    private fun Char.isCharacter(): Boolean{
        return (this == '+') or (this == '-') or (this == '*') or (this == '/') or (this == ':')or (this == '×') or (this == '÷')
    }

}