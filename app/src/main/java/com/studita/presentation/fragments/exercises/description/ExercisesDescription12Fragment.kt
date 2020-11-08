package com.studita.presentation.fragments.exercises.description

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.studita.R
import com.studita.domain.entity.exercise.ExercisesDescriptionData
import com.studita.utils.getAllViewsOfTypeT
import com.studita.utils.injectParts
import kotlinx.android.synthetic.main.exercises_description_12_layout.*
import kotlinx.android.synthetic.main.exercises_description_7_layout.*

class ExercisesDescription12Fragment :
    ExercisesDescriptionFragment(R.layout.exercises_description_12_layout) {

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
                exercisesDescription12ParentLinearLayout.getAllViewsOfTypeT<TextView>()[index + 1]
            val injected = injectParts(
                context!!,
                exercisesDescriptionModel.textParts[index],
                exercisesDescriptionModel.partsToInject!!
            )
            child.text = injected.first
        }
    }
}