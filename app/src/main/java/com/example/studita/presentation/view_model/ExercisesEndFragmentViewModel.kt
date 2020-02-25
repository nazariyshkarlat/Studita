package com.example.studita.presentation.view_model

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.example.studita.presentation.fragments.ExercisesDetailedStatFragment
import com.example.studita.presentation.fragments.ExercisesEndFragment
import com.example.studita.presentation.fragments.ExercisesResultFragment

class ExercisesEndFragmentViewModel : ViewModel(){

    val buttonsLayoutDividerPadding = SingleLiveEvent<Pair<Int, Int>>()

    fun setScrollViewDividerAndPadding(res: Int, paddingTop: Int){
        buttonsLayoutDividerPadding.value = res to paddingTop
    }

    fun getExercisesDetailedStatFragment(exercisesEndFragment: ExercisesEndFragment): ExercisesDetailedStatFragment =
        with(exercisesEndFragment) {
            val fragment = ExercisesDetailedStatFragment()
            val bundle = Bundle()
            arguments?.getInt("TRUE_ANSWERS")?.let { bundle.putInt("TRUE_ANSWERS", it) }
            arguments?.getInt("FALSE_ANSWERS")?.let { bundle.putInt("FALSE_ANSWERS", it) }
            arguments?.getInt("PROCESS_SECONDS")?.let { bundle.putInt("PROCESS_SECONDS", it) }
            fragment.arguments = bundle
            fragment
        }

    fun getExercisesResultFragment(exercisesEndFragment: ExercisesEndFragment): ExercisesResultFragment =
        with(exercisesEndFragment) {
            val fragment = ExercisesResultFragment()
            val bundle = Bundle()
            arguments?.getInt("TRUE_ANSWERS")?.let { bundle.putInt("TRUE_ANSWERS", it) }
            arguments?.getInt("ANSWERS_PERCENT")?.let { bundle.putInt("ANSWERS_PERCENT", it) }
            fragment.arguments = bundle
            fragment
        }

}