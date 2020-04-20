package com.example.studita.presentation.fragments.exercises.screen

import android.text.SpannableString
import android.text.SpannableStringBuilder
import androidx.core.content.ContextCompat
import com.example.studita.R
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.utils.createSpannableString
import kotlinx.android.synthetic.main.exercise_screen_type_1.*
import kotlinx.android.synthetic.main.exercise_screen_type_3.*
import java.util.regex.Pattern

open class ExerciseScreen(viewId: Int) : NavigatableFragment(viewId){

    protected fun injectParts(subtitle: String, partsToInject: List<String>): SpannableStringBuilder{
        val m =
            Pattern.compile("\\{.*?\\}").matcher(subtitle)
        var spanIndex = 1
        val builder = SpannableStringBuilder()
        while (m.find()) {
            val insideBrackets =
                partsToInject[m.group(0).replace(
                    """[{}]""".toRegex(),
                    ""
                ).toInt()]
            val textSpanParts: ArrayList<SpannableString> = ArrayList(subtitle.split(
                "\\{.*?\\}".toRegex()).map{span -> SpannableString(span) })
            textSpanParts.add(spanIndex, insideBrackets.createSpannableString(color = ContextCompat.getColor(exerciseScreenType1Subtitle.context, R.color.green)))
            textSpanParts.forEach{part-> builder.append(part)}
            spanIndex++
        }
        return builder
    }

}