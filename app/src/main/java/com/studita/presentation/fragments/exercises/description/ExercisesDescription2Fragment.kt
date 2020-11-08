package com.studita.presentation.fragments.exercises.description

import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.studita.R
import com.studita.domain.entity.exercise.ExercisesDescriptionData
import com.studita.utils.createSpannableString
import com.studita.utils.getAllViewsOfTypeT
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
                child.text = part
        }
    }
}