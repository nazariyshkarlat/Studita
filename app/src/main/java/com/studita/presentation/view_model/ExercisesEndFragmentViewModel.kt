package com.studita.presentation.view_model

import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import com.studita.presentation.fragments.exercises.ExercisesDetailedStatFragment
import com.studita.presentation.fragments.exercises.ExercisesEndFragment
import com.studita.presentation.fragments.exercises.ExercisesResultFragment

class ExercisesEndFragmentViewModel : ViewModel() {

    val buttonsLayoutDividerPadding = SingleLiveEvent<Pair<Int, Int>>()

    fun setScrollViewDividerAndPadding(res: Int, paddingTop: Int) {
        buttonsLayoutDividerPadding.value = res to paddingTop
    }

    fun getExercisesDetailedStatFragment(exercisesEndFragment: ExercisesEndFragment): ExercisesDetailedStatFragment =
        with(exercisesEndFragment) {
            val fragment =
                ExercisesDetailedStatFragment()

            arguments?.let {
                val bundle = bundleOf(
                    "TRUE_ANSWERS" to it.getInt("TRUE_ANSWERS"),
                    "FALSE_ANSWERS" to it.getInt("FALSE_ANSWERS"),
                    "OBTAINED_XP" to it.getInt("OBTAINED_XP"),
                    "PROCESS_SECONDS" to it.getLong("PROCESS_SECONDS")
                )
                fragment.arguments = bundle
            }
            fragment
        }

    fun getExercisesResultFragment(exercisesEndFragment: ExercisesEndFragment): ExercisesResultFragment =
        with(exercisesEndFragment) {
            val fragment =
                ExercisesResultFragment()
            arguments?.let {
                val bundle = bundleOf(
                    "ANSWERS_PERCENT" to it.getFloat("ANSWERS_PERCENT"),
                    "OLD_USER_DATA" to it.getString("OLD_USER_DATA"),
                    "OBTAINED_XP" to it.getInt("OBTAINED_XP"),
                    "EXERCISES_BONUS_CORRECT_COUNT" to it.getInt("EXERCISES_BONUS_CORRECT_COUNT")
                )
                fragment.arguments = bundle
            }
            fragment
        }

}