package com.studita.presentation.fragments.exercises.explanation

import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.lifecycle.ViewModelProviders
import com.studita.R
import com.studita.domain.entity.exercise.ExerciseData
import com.studita.presentation.fragments.base.NavigatableFragment
import com.studita.presentation.fragments.exercises.ExercisesScrollableFragment
import com.studita.presentation.view_model.ExercisesViewModel
import com.studita.utils.createSpannableString
import kotlinx.android.synthetic.main.exercises_description_1_layout.*
import kotlinx.android.synthetic.main.explanation_1_layout.*
import java.util.regex.Pattern

class ExerciseExplanation1Fragment : ExercisesScrollableFragment(R.layout.explanation_1_layout){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel?.let {vm->
            if (vm.exerciseData is ExerciseData.ExerciseExplanationData){
                val explanationUiModel = (vm.exerciseData as ExerciseData.ExerciseExplanationData)

                view.post {
                    fillView(explanationUiModel.textParts)
                    if (!isHidden)
                        checkButtonDivider(view)
                }
            }
        }
    }

    private fun fillView(textParts: List<String>){
        var childIndex = -1
        explanation1ParentLinearLayout.children.forEach { child ->
            if (child is TextView) {
                if (childIndex >= 0) {
                    child.text = textParts[childIndex]
                }
                childIndex++
            }
        }
    }

}