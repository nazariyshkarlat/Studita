package com.example.studita.presentation.fragments.exercises.screen

import android.text.SpannableString
import android.text.SpannableStringBuilder
import androidx.core.content.ContextCompat
import com.example.studita.R
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.utils.createSpannableString
import java.util.regex.Pattern

open class ExerciseScreen(viewId: Int) : NavigatableFragment(viewId){

    protected fun injectParts(subtitle: String, partsToInject: List<String>): SpannableStringBuilder{
        val builder = SpannableStringBuilder()
        context?.let {
            val m =
                Pattern.compile("\\{.*?\\}").matcher(subtitle)
            var spanIndex = 1
            while (m.find()) {
                val insideBrackets =
                    partsToInject[m.group(0).replace(
                        """[{}]""".toRegex(),
                        ""
                    ).toInt()]
                val textSpanParts: ArrayList<SpannableString> = ArrayList(subtitle.split(
                    "\\{.*?\\}".toRegex()
                ).map { span -> SpannableString(span) })
                textSpanParts.add(
                    spanIndex,
                    insideBrackets.createSpannableString(
                        color = ContextCompat.getColor(
                            it,
                            R.color.green
                        )
                    )
                )
                textSpanParts.forEach { part -> builder.append(part) }
                spanIndex++
            }
        }
        return builder
    }

}