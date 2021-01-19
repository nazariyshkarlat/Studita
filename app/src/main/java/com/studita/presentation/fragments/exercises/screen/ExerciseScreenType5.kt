package com.studita.presentation.fragments.exercises.screen

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.studita.R
import com.studita.presentation.fragments.base.NavigatableFragment
import com.studita.presentation.model.ExerciseUiModel
import com.studita.presentation.view_model.ExercisesViewModel
import com.studita.utils.dp
import com.studita.utils.makeView
import kotlinx.android.synthetic.main.exercise_screen_type_5.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ExerciseScreenType5 : NavigatableFragment(R.layout.exercise_screen_type_5) {

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
        exerciseScreenType5SnapHorizontalScrollView.post {
            lifecycleScope.launch(Dispatchers.Main) {
                delay(500)
                if (view != null) {
                    ObjectAnimator.ofInt(
                        exerciseScreenType5SnapHorizontalScrollView,
                        "scrollX",
                        getScrollEndPos()
                    ).apply {
                        interpolator = FastOutSlowInInterpolator()
                    }.setDuration(2000).start()
                }
            }
        }

        platesText.forEachIndexed { idx, text ->
            val plateView =
                contentView.makeView(R.layout.exercise_screen_type_5_plate_item)
                    .apply {
                        val params = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                        if (idx != 0)
                            params.leftMargin = 8F.dp
                        if (idx != platesText.size - 1)
                            params.rightMargin = 8F.dp
                        this.layoutParams = params

                        (this as TextView).text = text
                    }
            contentView.addView(plateView)
        }

    }

    private fun getScrollEndPos() : Int{
        val lastChild = exerciseScreenType5SnapHorizontalScrollView.getChildAt(
            exerciseScreenType5SnapHorizontalScrollView.childCount - 1
        )
        val bottom =
            lastChild.right + exerciseScreenType5SnapHorizontalScrollView.paddingEnd
        return bottom - (exerciseScreenType5SnapHorizontalScrollView.scrollX + exerciseScreenType5SnapHorizontalScrollView.width)
    }

}