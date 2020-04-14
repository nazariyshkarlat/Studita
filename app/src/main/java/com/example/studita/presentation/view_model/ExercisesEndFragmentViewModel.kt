package com.example.studita.presentation.view_model

import android.os.Bundle
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
            val bundle = Bundle()
            arguments?.getInt("TRUE_ANSWERS")?.let { bundle.putInt("TRUE_ANSWERS", it) }
            arguments?.getInt("FALSE_ANSWERS")?.let { bundle.putInt("FALSE_ANSWERS", it) }
            arguments?.getInt("OBTAINED_XP")?.let { bundle.putInt("OBTAINED_XP", it) }
            arguments?.getLong("PROCESS_SECONDS")?.let { bundle.putLong("PROCESS_SECONDS", it) }
            fragment.arguments = bundle
            fragment
        }

    fun getExercisesResultFragment(exercisesEndFragment: ExercisesEndFragment): ExercisesResultFragment =
        with(exercisesEndFragment) {
            val fragment =
                ExercisesResultFragment()
            val bundle = Bundle()
            arguments?.getFloat("ANSWERS_PERCENT")?.let { bundle.putFloat("ANSWERS_PERCENT", it) }
            fragment.arguments = bundle
            fragment
        }

}