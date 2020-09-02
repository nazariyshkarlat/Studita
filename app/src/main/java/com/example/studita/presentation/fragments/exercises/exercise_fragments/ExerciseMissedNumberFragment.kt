package com.example.studita.presentation.fragments.exercises.exercise_fragments

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import com.example.studita.utils.hideKeyboard
import com.example.studita.utils.setAllClickable
import kotlinx.android.synthetic.main.exercise_input_collection_layout.*
import kotlinx.android.synthetic.main.exercise_input_missed_part_layout.*

class ExerciseMissedNumberFragment :
    NavigatableFragment(R.layout.exercise_input_missed_part_layout),
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
                        exerciseMissedPartLayoutKeyboard.setAllClickable(false)
                        exerciseMissedPartLayoutEditText.isFocusable = false
                    }
                })
            if (it.exerciseUiModel is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType10UiModel) {
                val exerciseUiModel =
                    it.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType10UiModel
                exerciseMissedPartLayoutLeftTextView.text = exerciseUiModel.titleParts.first
                exerciseMissedPartLayoutRightTextView.text = exerciseUiModel.titleParts.second
                exerciseMissedPartLayoutBottomTextView.text = exerciseUiModel.subtitle

                exerciseMissedPartLayoutEditText.keyListener =
                    DigitsKeyListener.getInstance("0123456789")
                if (exerciseUiModel.isNumeral) {
                    exerciseMissedPartLayoutEditText.filters += InputFilter.LengthFilter(1)
                }
            }
        }

        exerciseMissedPartLayoutEditText.addTextChangedListener(this)
        exerciseMissedPartLayoutKeyboard.syncWithTextView(exerciseMissedPartLayoutEditText)
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val str = s.toString()
        if (str.isNotEmpty()) {
            if (str[0] == '0') {
                exerciseMissedPartLayoutEditText.text.delete(0, 1)
                return
            } else {
                exercisesViewModel?.setButtonEnabled(true)
                exercisesViewModel?.exerciseRequestData =
                    ExerciseRequestData(str)
            }
        } else
            exercisesViewModel?.setButtonEnabled(false)
    }

}