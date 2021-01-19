package com.studita.presentation.fragments.exercises.screen

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.studita.R
import com.studita.presentation.fragments.base.NavigatableFragment
import com.studita.presentation.model.ExerciseImagesRowUiModel
import com.studita.presentation.model.ExerciseUiModel
import com.studita.presentation.view_model.ExercisesViewModel
import com.google.android.flexbox.FlexboxLayout
import com.studita.utils.dp
import kotlinx.android.synthetic.main.exercise_screen_type_1.*

class ExerciseScreenType1 : NavigatableFragment(R.layout.exercise_screen_type_1) {

    private var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesViewModel?.let {
            if (it.exerciseUiModel is ExerciseUiModel.ExerciseUiModelScreen.ScreenType1UiModel) {
                val screenUiModel =
                    it.exerciseUiModel as ExerciseUiModel.ExerciseUiModelScreen.ScreenType1UiModel
                exerciseScreenType1SymbolTitle.text = screenUiModel.title.symbol
                exerciseScreenType1SymbolNameTitle.text = screenUiModel.title.symbolName
                exerciseScreenType1Subtitle.text = screenUiModel.subtitle
                fillFlexBoxLayout(screenUiModel.imagesRowUiModel)
            }
        }

    }

    private fun fillFlexBoxLayout(imagesRowUiModel: ExerciseImagesRowUiModel) {
        for (i in 0 until imagesRowUiModel.count) {
            val shapeView = View(exerciseScreenType1FlexboxLayout.context)
            val params = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            )
            params.height = 24F.dp
            params.width = 24F.dp
            shapeView.layoutParams = params
            shapeView.background = imagesRowUiModel.image
            exerciseScreenType1FlexboxLayout.addView(shapeView)
        }
    }

}