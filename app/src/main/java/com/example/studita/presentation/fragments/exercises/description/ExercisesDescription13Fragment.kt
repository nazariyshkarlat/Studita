package com.example.studita.presentation.fragments.exercises.description

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExercisesDescriptionData
import com.example.studita.utils.getAllViewsOfTypeT
import com.example.studita.utils.injectParts
import kotlinx.android.synthetic.main.exercises_description_12_layout.*
import kotlinx.android.synthetic.main.exercises_description_13_layout.*
import kotlinx.android.synthetic.main.exercises_description_7_layout.*

class ExercisesDescription13Fragment :
    ExercisesDescriptionFragment(R.layout.exercises_description_13_layout) {

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
                exercisesDescription13ParentLinearLayout.getAllViewsOfTypeT<TextView>()[index + 1]
            val injected = injectParts(
                context!!,
                exercisesDescriptionModel.textParts[index],
                exercisesDescriptionModel.partsToInject!!
            )
            child.text = injected.first
        }
    }
}