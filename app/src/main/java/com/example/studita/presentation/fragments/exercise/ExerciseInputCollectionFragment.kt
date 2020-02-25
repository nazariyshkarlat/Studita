package com.example.studita.presentation.fragments.exercise

import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.presentation.extensions.hideKeyboard
import com.example.studita.presentation.extensions.makeView
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import com.example.studita.presentation.views.CustomTypefaceSpan
import kotlinx.android.synthetic.main.exercise_input_collection_fragment.*
import kotlinx.android.synthetic.main.exercise_input_collection_text_view.view.*
import kotlinx.android.synthetic.main.exercise_input_equation_fragment.*
import java.io.IOException

class ExerciseInputCollectionFragment : BaseFragment(R.layout.exercise_input_collection_fragment), TextWatcher {

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
                        exerciseInputCollectionFragmentEditText.isFocusable = false
                    }
                })
        }

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
        exerciseInputCollectionFragmentEditText.addTextChangedListener(this)
    }

    private fun makeMediumWord(word: String): SpannableStringBuilder{
        val builder = SpannableStringBuilder()
        val mediumWord = SpannableString(word)
        mediumWord.setSpan(
            CustomTypefaceSpan(
                " ",
                context?.let { ResourcesCompat.getFont(it, R.font.roboto_medium) }
            ), 0, mediumWord.length, 0
        )
        val str = resources.getString(R.string.exercise_type_8_9_hint).split("%1\$s")
        builder.append(str[0])
        builder.append(mediumWord)
        if((word == "натуральных") or (word == "ненатуральных"))
            builder.append(" чисел")
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
            exercisesViewModel?.exercisesButtonState?.value = true
            exercisesViewModel?.exerciseRequestData =
                ExerciseRequestData(s.toString())
        }else{
            exercisesViewModel?.exercisesButtonState?.value = false
        }
    }

}