package com.example.studita.presentation.fragments.description

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import com.example.studita.R
import kotlinx.android.synthetic.main.exercises_description_4_layout.*

class ExercisesDescription4Fragment : ExercisesDescriptionFragment(R.layout.exercises_description_4_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesDescriptionModel?.let {
            formView(it.textParts)
            checkButtonDivider(view)
        }
    }

    private fun formView(textParts: List<String>) {
        exercisesDescription4ParentLinearLayout.children.forEachIndexed { index, view ->
            if (index != 0) {
                if (view is TextView) {
                    view.text = textParts[index - 1]
                }
            }
        }
    }
}