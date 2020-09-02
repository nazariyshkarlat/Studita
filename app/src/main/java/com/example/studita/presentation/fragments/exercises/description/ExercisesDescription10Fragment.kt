package com.example.studita.presentation.fragments.exercises.description

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import com.example.studita.R
import kotlinx.android.synthetic.main.exercises_description_10_layout.*

class ExercisesDescription10Fragment :
    ExercisesDescriptionFragment(R.layout.exercises_description_10_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesDescriptionModel?.let {
            formView(it.textParts)

            if (!isHidden)
                checkButtonDivider(view)
        }
    }

    private fun formView(textParts: List<String>) {
        var childIndex = -1
        exercisesDescription10ParentLinearLayout.children.forEach { child ->
            if (child is TextView) {
                if (childIndex >= 0)
                    child.text = textParts[childIndex]
                childIndex++
            }
        }
    }
}