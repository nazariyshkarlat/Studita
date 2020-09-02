package com.example.studita.presentation.fragments.exercises.exercise_fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.ExercisesViewModel
import com.example.studita.utils.hideKeyboard
import com.example.studita.utils.setAllClickable
import kotlinx.android.synthetic.main.exercise_input_collection_layout.*
import kotlinx.android.synthetic.main.exercise_input_layout.*

class ExerciseInputFragment : NavigatableFragment(R.layout.exercise_input_layout), TextWatcher {

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
                        exerciseInputLayoutKeyboard.setAllClickable(false)
                        exerciseInputLayoutEditText.isFocusable = false
                    }
                })
        }

        exerciseInputLayoutEditText.addTextChangedListener(this)
        exerciseInputLayoutKeyboard.syncWithTextView(exerciseInputLayoutEditText)
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val str = s.toString()
        if (str.isNotEmpty()) {
            if (str[0] == '0') {
                exerciseInputLayoutEditText.text.delete(0, 1)
                return
            } else {
                exercisesViewModel?.exerciseRequestData = ExerciseRequestData(s.toString())
                exercisesViewModel?.buttonEnabledState?.value = true
            }
        } else {
            exercisesViewModel?.buttonEnabledState?.value = false
        }
    }

}