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
import com.example.studita.utils.ThemeUtils
import com.example.studita.utils.createSpannableString
import com.example.studita.utils.dpToPx
import com.example.studita.utils.injectParts
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
        var insideBrackets: String? = null
        exercisesDescription1ParentLinearLayout.children.forEach { child ->
            if (child is TextView) {
                if (childIndex >= 0) {
                    val injected = injectParts(context!!, exercisesDescriptionModel.textParts[childIndex], exercisesDescriptionModel.partsToInject!!)
                    child.text = injected.first
                    insideBrackets = injected.second
                }
                childIndex++
            } else {
                if (child is LinearLayout)
                    fillLinearLayout(child, insideBrackets!!.toInt())
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
            params.height = 24F.dpToPx()
            params.width = 24F.dpToPx()
            if (i != imgCount - 1)
                params.marginEnd = 12F.dpToPx()
            shapeView.layoutParams = params
            shapeView.background =
                ContextCompat.getDrawable(child.context, R.drawable.slightly_smiling_face)
            child.addView(shapeView)
        }
    }

}