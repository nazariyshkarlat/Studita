package com.example.studita.presentation.fragments.exercises.screen

import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.utils.createSpannableString
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercise_screen_type_3.*
import java.util.regex.Pattern

class ExerciseScreenType3 : ExerciseScreen(R.layout.exercise_screen_type_3){

    private var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesViewModel?.let {
            if(it.exerciseUiModel is ExerciseUiModel.ExerciseUiModelScreen.ScreenType3UiModel){
                val screenUiModel = it.exerciseUiModel as ExerciseUiModel.ExerciseUiModelScreen.ScreenType3UiModel
                exerciseScreenType3Title.text = screenUiModel.title
                exerciseScreenType3Subtitle.text = injectParts(screenUiModel.subtitle, screenUiModel.partsToInject)
            }
        }
    }

}