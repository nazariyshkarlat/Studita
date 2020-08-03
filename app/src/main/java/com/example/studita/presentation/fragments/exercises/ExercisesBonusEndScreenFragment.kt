package com.example.studita.presentation.fragments.exercises

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.ExercisesViewModel
import com.example.studita.utils.LanguageUtils
import com.example.studita.utils.navigateTo
import kotlinx.android.synthetic.main.exercises_bonus_end_layout.*

class ExercisesBonusEndScreenFragment : NavigatableFragment(R.layout.exercises_bonus_end_layout){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesViewModel?.let {vm->
            vm.progressState.observe(
                viewLifecycleOwner,
                getProgressStateObserver(vm)
            )
        }

        arguments?.let{
            exercisesBonusEndLayoutTitle.text = resources.getString(R.string.exercises_bonus_end_layout_title_template, it.getInt("OBTAINED_XP"))
            exercisesBonusEndLayoutSubtitle.text = LanguageUtils.getResourcesRussianLocale(view.context)?.getQuantityString(R.plurals.exercises_bonus_end_layout_subtitle_plurals_template,it.getInt("CORRECT_ANSWERS_COUNT"), it.getInt("CORRECT_ANSWERS_COUNT"))
            exercisesBonusEndLayoutButton.setOnClickListener {
                exercisesViewModel?.endExercises()
            }
        }

    }

    private fun getProgressStateObserver(exercisesViewModel: ExercisesViewModel) =
        Observer<Pair<Float, Boolean>> { pair ->
            val last = pair.second
            if(last){
                exercisesBonusEndLayoutButton.setOnClickListener {}
                exercisesViewModel.saveUserDataState.observe(viewLifecycleOwner, Observer {
                    val saved = it.first
                    if(saved){
                        (activity as AppCompatActivity).navigateTo(exercisesViewModel.getExercisesEndFragment(it.second), R.id.frameLayout)
                    }
                })
            }
        }

}