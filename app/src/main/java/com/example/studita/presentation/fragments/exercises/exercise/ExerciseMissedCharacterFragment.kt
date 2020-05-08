package com.example.studita.presentation.fragments.exercises.exercise

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.utils.hideKeyboard
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercise_input_missed_part_layout.*

class ExerciseMissedCharacterFragment : NavigatableFragment(R.layout.exercise_input_missed_part_layout), TextWatcher {

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
                        exerciseMissedPartLayoutEditText.isFocusable = false
                    }
                })
        }

        /*
        val exerciseUiModel = arguments?.getParcelable<ExerciseUiModel>("EXERCISE")
        if (exerciseUiModel is ExerciseUiModel.ExerciseUi4) {
            exerciseCharacterFragmentLeftTextView.text = exerciseUiModel.expressionParts[0]
            exerciseCharacterFragmentRightTextView.text = resources.getString(R.string.exercise_type_4_right_text, exerciseUiModel.expressionParts[1], exerciseUiModel.expressionResult)
            exerciseCharacterFragmentBottomTextView.text = resources.getString(R.string.exercise_type_4_bottom_text)
        }
         */
        exerciseMissedPartLayoutEditText.keyListener = DigitsKeyListener.getInstance("+-*:÷×/")
        exerciseMissedPartLayoutEditText.addTextChangedListener(this)
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val str = s.toString()
        if(str.isNotEmpty()){
            if (str[0] == '0')
                exerciseMissedPartLayoutEditText.setText(
                    exerciseMissedPartLayoutEditText.text.substring(
                        1,
                        exerciseMissedPartLayoutEditText.text.length
                    )
                )else {
                exercisesViewModel?.buttonEnabledState?.value = true
                exercisesViewModel?.exerciseRequestData =
                    ExerciseRequestData(str)
            }
        }else
            exercisesViewModel?.buttonEnabledState?.value = false
    }

}