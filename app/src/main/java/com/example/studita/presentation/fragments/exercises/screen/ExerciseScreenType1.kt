package com.example.studita.presentation.fragments.exercises.screen

import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.utils.createSpannableString
import com.example.studita.presentation.utils.dpToPx
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import com.google.android.flexbox.FlexboxLayout
import kotlinx.android.synthetic.main.exercise_screen_type_1.*
import java.util.regex.Pattern

class ExerciseScreenType1 :  NavigatableFragment(R.layout.exercise_screen_type_1){

    private var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesViewModel?.let {
            if(it.exerciseUiModel is ExerciseUiModel.ExerciseUiModelScreen.ScreenType1UiModel){
                val screenUiModel = it.exerciseUiModel as ExerciseUiModel.ExerciseUiModelScreen.ScreenType1UiModel
                exerciseScreenType1Title.text = screenUiModel.title
                exerciseScreenType1Subtitle.text = screenUiModel.subtitle
                for(i in 0 until screenUiModel.title.toInt()) {
                    val shapeView = View(exerciseScreenType1FlexboxLayout.context)
                    val params = FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT)
                    params.height = 32.dpToPx()
                    params.width = 32.dpToPx()
                    shapeView.layoutParams = params
                    shapeView.background =  ContextCompat.getDrawable(exerciseScreenType1FlexboxLayout.context, R.drawable.exercise_rectangle)
                    exerciseScreenType1FlexboxLayout.addView(shapeView)
                }
                injectParts(screenUiModel)
            }
        }

    }

    private fun injectParts(screenUiModel: ExerciseUiModel.ExerciseUiModelScreen.ScreenType1UiModel){
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
            textSpanParts.add(spanIndex, insideBrackets.createSpannableString(typeFace = ResourcesCompat.getFont(exerciseScreenType1Subtitle.context, R.font.roboto_medium)))
            textSpanParts.forEach{part-> builder.append(part)}
            spanIndex++
        }
        exerciseScreenType1Subtitle.text = builder
    }

}