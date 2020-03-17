package com.example.studita.presentation.fragments.exercise_screen

import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.extensions.createSpannableString
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercise_screen_type_1.*
import kotlinx.android.synthetic.main.exercise_screen_type_2.*
import kotlinx.android.synthetic.main.exercise_screen_type_3.*
import java.util.regex.Pattern

class ExerciseScreenType3 : NavigatableFragment(R.layout.exercise_screen_type_3){

    private var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesViewModel?.let {
            if(it.exerciseUiModel is ExerciseUiModel.ExerciseUiModelScreen.ScreenType3UiModel){
                val screenUiModel = it.exerciseUiModel as ExerciseUiModel.ExerciseUiModelScreen.ScreenType3UiModel
                exerciseScreenType3Title.text = screenUiModel.title
                injectParts(screenUiModel)
            }
        }
    }

    private fun injectParts(screenUiModel: ExerciseUiModel.ExerciseUiModelScreen.ScreenType3UiModel){
        val text = screenUiModel.subtitle
        val m =
            Pattern.compile("\\{.*?\\}").matcher(text)
        var spanIndex = 1
        val builder = SpannableStringBuilder()
        while (m.find()) {
            val insideBrackets =
                screenUiModel.partsToInject[m.group(0).replace(
                    """[{}]""".toRegex(),
                    ""
                ).toInt()]
            val textSpanParts: ArrayList<SpannableString> = ArrayList(text.split(
                "\\{.*?\\}".toRegex()).map{span -> SpannableString(span) })
            textSpanParts.add(spanIndex, insideBrackets.createSpannableString(color = ContextCompat.getColor(exerciseScreenType3Subtitle.context, R.color.green), typeFace = ResourcesCompat.getFont(exerciseScreenType3Subtitle.context, R.font.roboto_medium)))
            textSpanParts.forEach{part-> builder.append(part)}
            spanIndex++
        }
        exerciseScreenType3Subtitle.text = builder
    }

}