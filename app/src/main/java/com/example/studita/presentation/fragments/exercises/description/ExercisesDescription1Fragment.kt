package com.example.studita.presentation.fragments.exercises.description

import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.OneShotPreDrawListener
import androidx.core.view.children
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExercisesDescriptionData
import com.example.studita.utils.createSpannableString
import com.example.studita.utils.dpToPx
import kotlinx.android.synthetic.main.exercises_description_1_layout.*
import java.util.regex.Pattern


class ExercisesDescription1Fragment :
    ExercisesDescriptionFragment(R.layout.exercises_description_1_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesDescriptionModel?.let {
            OneShotPreDrawListener.add(exercisesDescription1ParentLinearLayout) {
                formView(it)

                if (!isHidden)
                    checkButtonDivider(view)
            }
        }
    }

    private fun formView(exercisesDescriptionModel: ExercisesDescriptionData) {
        var childIndex = -1
        var insideBrackets = "0"
        exercisesDescription1ParentLinearLayout.children.forEach { child ->
            if (child is TextView) {
                if (childIndex >= 0) {
                    val text = exercisesDescriptionModel.textParts[childIndex]
                    val m =
                        Pattern.compile("\\{.*?\\}").matcher(text)
                    var spanIndex = 1
                    val builder = SpannableStringBuilder()
                    while (m.find()) {
                        insideBrackets =
                            exercisesDescriptionModel.partsToInject!![m.group(0).replace(
                                """[{}]""".toRegex(),
                                ""
                            ).toInt()]
                        val textSpanParts: ArrayList<SpannableString> = ArrayList(text.split(
                            "\\{.*?\\}".toRegex()
                        ).map { span -> SpannableString(span) })
                        textSpanParts.add(
                            spanIndex,
                            insideBrackets.createSpannableString(
                                color = ContextCompat.getColor(
                                    child.context,
                                    R.color.yellow
                                )
                            )
                        )
                        textSpanParts.forEach { part -> builder.append(part) }
                        spanIndex++
                    }
                    if (spanIndex == 1)
                        child.text = text
                    else
                        child.text = builder
                }
                childIndex++
            } else {
                if (child is LinearLayout)
                    fillLinearLayout(child, insideBrackets.toInt())
            }
        }
    }

    private fun fillLinearLayout(child: LinearLayout, imgCount: Int) {
        for (i in 0 until imgCount) {
            val shapeView = View(child.context)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.height = 24.dpToPx()
            params.width = 24.dpToPx()
            if (i != imgCount - 1)
                params.marginEnd = 12.dpToPx()
            shapeView.layoutParams = params
            shapeView.background =
                ContextCompat.getDrawable(child.context, R.drawable.slightly_smiling_face)
            child.addView(shapeView)
        }
    }

}