package com.example.studita.presentation.fragments.exercises.exercise_fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import com.example.studita.presentation.views.ArrangeNumbersView
import kotlinx.android.synthetic.main.exercise_arrange_numbers_layout.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ExerciseArrangeNumbersFragment : NavigatableFragment(R.layout.exercise_arrange_numbers_layout), ArrangeNumbersView.OnNewAnswerListener {

    private var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesViewModel?.let {vm->
            when (vm.exerciseUiModel) {
                is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType19UiModel -> {
                    val exerciseUiModel = vm.exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise.ExerciseType19UiModel
                    exerciseArrangeNumbersLayoutTitle.text = exerciseUiModel.title
                    exerciseArrangeNumbersLayoutNumbersView.setUpVariants(exerciseUiModel.variants.map { (it.meta ?: it.variantText).toInt() }, this)
                }
            }
        }
    }

    override fun onViewIsFilled(correctAnswersCount: Int) {
        exercisesViewModel?.let{
            it.viewModelScope.launch {
                it.correctBonusAnswers =
                    exerciseArrangeNumbersLayoutNumbersView.correctAnswersCount
                delay(500)
                if(!it.isBonusCompleted) {
                    it.navigateBonuses()
                }
            }
        }
    }

    override fun onTrueAnswer(correctAnswersCount: Int) {
        exercisesViewModel?.correctBonusAnswers = correctAnswersCount
    }

}