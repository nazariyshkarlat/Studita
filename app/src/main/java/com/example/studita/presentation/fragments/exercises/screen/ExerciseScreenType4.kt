package com.example.studita.presentation.fragments.exercises.screen

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercise_screen_type_4.*

class ExerciseScreenType4 : ExerciseScreen(R.layout.exercise_screen_type_4) {

    private var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesViewModel?.let {
            if (it.exerciseUiModel is ExerciseUiModel.ExerciseUiModelScreen.ScreenType4UiModel) {
                val screenUiModel =
                    it.exerciseUiModel as ExerciseUiModel.ExerciseUiModelScreen.ScreenType4UiModel
                exerciseScreenType4Title.text = screenUiModel.title
                exerciseScreenType4Subtitle.text = screenUiModel.subtitle
                exerciseScreenType4Image.background = screenUiModel.image
            }
        }
    }

}