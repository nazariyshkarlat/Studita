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
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercise_input_fragment.*
import kotlinx.android.synthetic.main.exercise_variants_title_fragment.*

class ExerciseInputFragment : NavigatableFragment(R.layout.exercise_input_fragment), TextWatcher{

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
                    if(answered){
                        (activity as AppCompatActivity).hideKeyboard()
                        exerciseInputEditText.isFocusable = false
                        }
                })
        }

        /*
        val exerciseUiModel = arguments?.getParcelable<ExerciseUiModel>("EXERCISE")
        if (exerciseUiModel is ExerciseUiModel.ExerciseUi3) {
            exerciseInputFragmentTextView.text = resources.getString(R.string.exercise_type_2_3_title, exerciseUiModel.equation)
            exerciseInputEditText.hint = resources.getString(R.string.exercise_type_3_hint)
        }
         */
        exerciseInputEditText.addTextChangedListener(this)
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val str = s.toString()
        if(str.length == 1) {
            exercisesViewModel?.exercisesButtonState?.value = true
        }else if(str.isEmpty()){
            exercisesViewModel?.exercisesButtonState?.value = false
        }
        exercisesViewModel?.exerciseRequestData = ExerciseRequestData(s.toString())
    }

}