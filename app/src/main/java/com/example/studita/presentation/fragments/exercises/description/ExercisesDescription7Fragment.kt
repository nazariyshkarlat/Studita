package com.example.studita.presentation.fragments.exercises.description

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.studita.R
import com.example.studita.utils.ColorUtils
import com.example.studita.utils.dpToPx
import kotlinx.android.synthetic.main.exercises_description_pure_layout.*

class ExercisesDescription7Fragment : ExercisesDescriptionFragment(R.layout.exercises_description_pure_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesDescriptionModel?.let {
            formView(it.textParts)
            checkButtonDivider(view)
        }
    }

    private fun formView(textParts: List<String>) {
        val textColor = context?.let { ColorUtils.getPrimaryColor(it) } ?: 0
        val margin = 16.dpToPx()
        textParts.forEachIndexed { index, s ->
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val textView = TextView(context)
            textView.text = s
            textView.textSize = 18F
            textView.setTextColor(textColor)
            params.topMargin = if((index == 7) or (index == 8))
                24.dpToPx()
            else
                margin
            if(index == textParts.lastIndex)
                params.bottomMargin = margin
            textView.layoutParams = params
            exercisesDescriptionPureLayoutParentLinearLayout.addView(textView)
        }
    }
}