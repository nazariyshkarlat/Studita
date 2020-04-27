package com.example.studita.presentation.view_model

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import com.example.studita.presentation.fragments.exercises.ExercisesDetailedStatFragment
import com.example.studita.presentation.fragments.exercises.ExercisesEndFragment
import com.example.studita.presentation.fragments.exercises.ExercisesResultFragment

class ExercisesEndFragmentViewModel : ViewModel(){

    val buttonsLayoutDividerPadding = SingleLiveEvent<Pair<Int, Int>>()

    fun setScrollViewDividerAndPadding(res: Int, paddingTop: Int){
        buttonsLayoutDividerPadding.value = res to paddingTop
    }

    fun getExercisesDetailedStatFragment(exercisesEndFragment: ExercisesEndFragment): ExercisesDetailedStatFragment =
        with(exercisesEndFragment) {
            val fragment =
                ExercisesDetailedStatFragment()

            arguments?.let {
                val bundle = bundleOf("TRUE_ANSWERS" to it.getInt("TRUE_ANSWERS"),
                    "FALSE_ANSWERS" to  it.getInt("FALSE_ANSWERS"),
                    "OBTAINED_XP" to it.getInt("OBTAINED_XP"),
                    "PROCESS_SECONDS" to it.getLong("PROCESS_SECONDS"))
                fragment.arguments = bundle
            }
            fragment
        }

    fun getExercisesResultFragment(exercisesEndFragment: ExercisesEndFragment): ExercisesResultFragment =
        with(exercisesEndFragment) {
            val fragment =
                ExercisesResultFragment()
            arguments?.let {
                val bundle =  bundleOf("ANSWERS_PERCENT" to it.getFloat("ANSWERS_PERCENT"))
                fragment.arguments = bundle
            }
            fragment
        }

}