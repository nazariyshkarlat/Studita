package com.example.studita.presentation.fragments.exercises.description

import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExercisesDescriptionData
import com.example.studita.utils.createSpannableString
import com.example.studita.utils.getAllViewsOfTypeT
import kotlinx.android.synthetic.main.exercises_description_2_layout.*
import java.util.regex.Pattern

class ExercisesDescription2Fragment :
    ExercisesDescriptionFragment(R.layout.exercises_description_2_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesDescriptionModel?.let {
            formView(it)

            if (!isHidden)
                checkButtonDivider(view)
        }
    }

    private fun formView(exercisesDescriptionModel: ExercisesDescriptionData) {
        exercisesDescriptionModel.textParts.forEachIndexed { index, part ->
            val child =
                exercisesDescription2ParentLinearLayout.getAllViewsOfTypeT<TextView>()[index + 1]
            val m =
                Pattern.compile("\\{.*?\\}").matcher(part)
            val builder = SpannableStringBuilder()
            if (m.find()) {
                val insideBrackets =
                    exercisesDescriptionModel.partsToInject!![m.group(0).replace(
                        """[{}]""".toRegex(),
                        ""
                    ).toInt()]
                val textSpanParts: ArrayList<SpannableString> = ArrayList(part.split(
                    "\\{.*?\\}".toRegex()
                ).map { span -> SpannableString(span) })
                textSpanParts.add(
                    0,
                    insideBrackets.createSpannableString(
                        color = ContextCompat.getColor(
                            child.context,
                            R.color.yellow
                        )
                    )
                )
                textSpanParts.forEach { textPartPart -> builder.append(textPartPart) }
                child.text = builder
            } else {
                child.text = part
            }
        }
    }
}