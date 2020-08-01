package com.example.studita.presentation.fragments.exercises.exercise

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.ExercisesViewModel
import com.example.studita.utils.LanguageUtils
import com.example.studita.utils.UserUtils
import com.example.studita.utils.navigateTo
import kotlinx.android.synthetic.main.exercise_bonus_end_layout.*

class ExercisesBonusEndScreenFragment : NavigatableFragment(R.layout.exercise_bonus_end_layout){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        arguments?.let{
            exerciseBonusEndLayoutTitle.text = resources.getString(R.string.exercises_bonus_end_layout_title_template, it.getInt("OBTAINED_XP"))
            exerciseBonusEndLayoutSubtitle.text = LanguageUtils.getResourcesRussianLocale(view.context)?.getQuantityString(R.plurals.exercises_bonus_end_layout_subtitle_plurals_template,it.getInt("CORRECT_ANSWERS_COUNT"), it.getInt("CORRECT_ANSWERS_COUNT"))
            exerciseBonusEndLayoutButton.setOnClickListener {
                exercisesViewModel?.getExercisesEndFragment(UserUtils.userData)?.let { it1 ->
                    (activity as AppCompatActivity).navigateTo(
                        it1, R.id.frameLayout)
                }
            }
        }

    }

}