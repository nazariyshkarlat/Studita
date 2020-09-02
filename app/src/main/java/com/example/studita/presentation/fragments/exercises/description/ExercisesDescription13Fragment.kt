package com.example.studita.presentation.fragments.exercises.description

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.studita.R
import com.example.studita.utils.getAllViewsOfTypeT
import kotlinx.android.synthetic.main.exercises_description_12_layout.*
import kotlinx.android.synthetic.main.exercises_description_13_layout.*

class ExercisesDescription13Fragment :
    ExercisesDescriptionFragment(R.layout.exercises_description_13_layout) {

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
        exercisesDescription13ParentLinearLayout.getAllViewsOfTypeT<TextView>().forEach { child ->
            if (childIndex >= 0)
                child.text = textParts[childIndex]
            childIndex++
        }
    }
}