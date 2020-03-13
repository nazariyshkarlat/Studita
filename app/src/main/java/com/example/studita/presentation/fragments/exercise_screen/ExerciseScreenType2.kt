package com.example.studita.presentation.fragments.exercise_screen

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.extensions.dpToPx
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercise_screen_type_1.*
import kotlinx.android.synthetic.main.exercise_screen_type_2.*


class ExerciseScreenType2 :  NavigatableFragment(R.layout.exercise_screen_type_2){

    private var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesViewModel?.let {
            if(it.exerciseUiModel is ExerciseUiModel.ExerciseUiModelScreen.ScreenType2UiModel){
                val screenUiModel = it.exerciseUiModel as ExerciseUiModel.ExerciseUiModelScreen.ScreenType2UiModel
                exerciseScreenType2Text.text = screenUiModel.title
            }
        }

    }

}