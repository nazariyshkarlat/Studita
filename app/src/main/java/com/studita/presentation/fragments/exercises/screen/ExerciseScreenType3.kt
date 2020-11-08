package com.studita.presentation.fragments.exercises.screen

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.studita.R
import com.studita.presentation.fragments.base.NavigatableFragment
import com.studita.presentation.model.ExerciseUiModel
import com.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercise_screen_type_3.*

class ExerciseScreenType3 : NavigatableFragment(R.layout.exercise_screen_type_3) {

    private var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesViewModel?.let {
            if (it.exerciseUiModel is ExerciseUiModel.ExerciseUiModelScreen.ScreenType3UiModel) {
                val screenUiModel =
                    it.exerciseUiModel as ExerciseUiModel.ExerciseUiModelScreen.ScreenType3UiModel
                exerciseScreenType3Title.text = screenUiModel.title
                exerciseScreenType3Subtitle.text = screenUiModel.subtitle
            }
        }
    }

}