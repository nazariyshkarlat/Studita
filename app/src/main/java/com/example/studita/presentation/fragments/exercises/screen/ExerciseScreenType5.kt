package com.example.studita.presentation.fragments.exercises.screen

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import com.example.studita.utils.dpToPx
import com.example.studita.utils.makeView
import kotlinx.android.synthetic.main.exercise_screen_type_5.*

class ExerciseScreenType5 : ExerciseScreen(R.layout.exercise_screen_type_5) {

    private var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesViewModel?.let {
            if (it.exerciseUiModel is ExerciseUiModel.ExerciseUiModelScreen.ScreenType5UiModel) {
                val screenUiModel =
                    it.exerciseUiModel as ExerciseUiModel.ExerciseUiModelScreen.ScreenType5UiModel
                exerciseScreenType5SymbolTitle.text = screenUiModel.title.symbol
                exerciseScreenType5SymbolNameTitle.text = screenUiModel.title.symbolName
                fillHorizontalPlatesView(view.context, screenUiModel.variants)
            }
        }
    }

    private fun fillHorizontalPlatesView(context: Context, platesText: List<String>) {
        val contentView = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        exerciseScreenType5SnapHorizontalScrollView.addView(contentView)

        platesText.forEachIndexed { idx, text ->
            val plateView =
                contentView.makeView(R.layout.exercise_screen_type_5_plate_item)
                    .apply {
                        val params = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                        if (idx != 0)
                            params.leftMargin = 8.dpToPx()
                        if (idx != platesText.size - 1)
                            params.rightMargin = 8.dpToPx()
                        this.layoutParams = params

                        (this as TextView).text = text
                    }
            contentView.addView(plateView)
        }

    }

}