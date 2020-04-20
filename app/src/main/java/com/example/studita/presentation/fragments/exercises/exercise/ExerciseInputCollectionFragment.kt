package com.example.studita.presentation.fragments.exercises.exercise

import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.domain.entity.exercise.ExerciseType11Filter
import com.example.studita.presentation.utils.createSpannableString
import com.example.studita.presentation.utils.hideKeyboard
import com.example.studita.presentation.utils.makeView
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercise_input_collection_layout.*
import kotlinx.android.synthetic.main.exercise_input_collection_text_view.view.*

class ExerciseInputCollectionFragment : NavigatableFragment(R.layout.exercise_input_collection_layout), TextWatcher {

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
                        exerciseInputCollectionLayoutEditText.isFocusable = false
                    }
                })

            if(it.exerciseUiModel is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType11UiModel){
                val exerciseUiModel = it.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType11UiModel
                for(number in exerciseUiModel.titleParts) {
                    val numberView = exerciseInputCollectionLayoutLinearLayout.makeView(R.layout.exercise_input_collection_text_view)
                    numberView.exerciseInputCollectionTextView.text = number
                    exerciseInputCollectionLayoutLinearLayout.addView(numberView)
            }
            exerciseInputCollectionLayoutEditText.hint = formTextViewHint(exerciseUiModel.filter, exerciseUiModel.compareNumber)
            }
        }

        /*
        when(val exerciseUiModel = arguments?.getParcelable<ExerciseUiModel>("EXERCISE")){
            is ExerciseUiModel.ExerciseUi8 ->{
                for(shape in exerciseUiModel.shapes) {
                    val shapeView = exerciseInputCollectionFragmentLinearLayout.makeView(R.layout.exercise_input_collection_image_view)
                    (shapeView as ImageView).setImageResource(
                        when(shape){
                            "circle" -> R.drawable.exercise_circle
                            "rectangle" -> R.drawable.exercise_rectangle
                            else -> throw IOException("unknown exercise type 8 collection shape")
                        }
                    )
                    exerciseInputCollectionFragmentLinearLayout.addView(shapeView)
                }
                exerciseInputCollectionFragmentEditText.hint =makeMediumWord(
                    when(exerciseUiModel.desiredShape){
                        "circle" -> "кружков"
                        "rectangle" -> "квадратов"
                        else -> throw IOException("unknown exercise type 8 desired shape")
                    }
                )
            }
            is ExerciseUiModel.ExerciseUi9 ->{
                for(number in exerciseUiModel.numbers) {
                    val numberView = exerciseInputCollectionFragmentLinearLayout.makeView(R.layout.exercise_input_collection_text_view)
                    numberView.exerciseInputCollectionTextView.text = number
                    exerciseInputCollectionFragmentLinearLayout.addView(numberView)
                }
                exerciseInputCollectionFragmentEditText.hint = makeMediumWord(when(exerciseUiModel.desiredType){
                    "natural" -> "натуральных"
                    "unnatural" -> "ненатуральных"
                    else -> throw IOException("unknown exercise type 9 desired type")
                })

            }
        }
         */
        exerciseInputCollectionLayoutEditText.addTextChangedListener(this)
    }

    private fun formTextViewHint(filter: ExerciseType11Filter, compareNumber: String): SpannableStringBuilder{
        val builder = SpannableStringBuilder()
        val filterText = when(filter){
            ExerciseType11Filter.BIGGER -> resources.getString(R.string.bigger)
            ExerciseType11Filter.LOWER -> resources.getString(R.string.lower)
        }

        val mediumText = "$filterText $compareNumber"

        val mediumSpan = mediumText.createSpannableString(typeFace = context?.let { ResourcesCompat.getFont(it, R.font.roboto_medium) })
        val str = resources.getString(R.string.exercise_type_11_hint).split("%1\$s")
        builder.append(str[0])
        builder.append(mediumSpan)
        builder.append(str[1])
        return builder
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val str = s.toString()
        if(str.isNotEmpty()) {
            if (str[0] == '0')
                exerciseInputCollectionLayoutEditText.setText(
                exerciseInputCollectionLayoutEditText.text.substring(
                    1,
                    exerciseInputCollectionLayoutEditText.text.length
                )
            )else {
                exercisesViewModel?.buttonEnabledState?.value = true
                exercisesViewModel?.exerciseRequestData =
                    ExerciseRequestData(s.toString())
            }
        }else{
            exercisesViewModel?.buttonEnabledState?.value = false
        }
    }

}