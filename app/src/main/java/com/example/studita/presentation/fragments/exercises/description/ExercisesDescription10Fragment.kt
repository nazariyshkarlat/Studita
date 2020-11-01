package com.example.studita.presentation.fragments.exercises.description

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExercisesDescriptionData
import com.example.studita.utils.getAllViewsOfTypeT
import com.example.studita.utils.injectParts
import kotlinx.android.synthetic.main.exercises_description_10_layout.*
import kotlinx.android.synthetic.main.exercises_description_7_layout.*
import kotlinx.android.synthetic.main.exercises_description_9_layout.*

class ExercisesDescription10Fragment :
    ExercisesDescriptionFragment(R.layout.exercises_description_10_layout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesDescriptionModel?.let {
            formView(it)

            if (!isHidden)
                checkButtonDivider(view)
        }
    }

    private fun formView(exercisesDescriptionModel: ExercisesDescriptionData) {
        var childIndex = 0
        exercisesDescription10ParentLinearLayout.children.forEach { child ->
            if (child is TextView) {
                if(childIndex != 0) {
                    val injected = injectParts(
                        context!!,
                        exercisesDescriptionModel.textParts[childIndex-1],
                        exercisesDescriptionModel.partsToInject!!
                    )
                    child.text = injected.first
                }
                childIndex++
            }
        }
    }
}