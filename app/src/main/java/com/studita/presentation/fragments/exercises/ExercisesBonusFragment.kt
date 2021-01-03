package com.studita.presentation.fragments.exercises

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.studita.R
import com.studita.presentation.fragments.base.NavigatableFragment
import com.studita.presentation.view_model.ExercisesViewModel
import com.studita.utils.*
import kotlinx.android.synthetic.main.exercise_bonus_layout.*

class ExercisesBonusFragment : NavigatableFragment(R.layout.exercise_bonus_layout) {

    var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesViewModel?.let { viewModel ->

            if (savedInstanceState == null) {
                viewModel.startSecondsCounter()
                (activity as AppCompatActivity).addFragment(
                    viewModel.initBonusFragment(),
                    R.id.exerciseBonusLayoutFrameLayout
                )
            }

            viewModel.exercisesBonusRemainingTimeState.observe(viewLifecycleOwner, Observer {

                if (it == 0L) {
                    viewModel.endBonusExercises()
                    showBonusEndFragment(viewModel.getExercisesBonusEndFragment())
                    return@Observer
                }

                exerciseBonusLayoutCounterText.text = if (it > 9) it.toString() else "0$it"

                if (it <= 5) {
                    exerciseBonusLayoutCounterIcon.setImageResource(R.drawable.ic_access_time_red)
                    exerciseBonusLayoutCounterText.setTextColor(
                        ThemeUtils.getRedColor(context!!)
                    )
                }

            })

            viewModel.exerciseBonusNavigationEvent.observe(viewLifecycleOwner, Observer {
                val bonusFragment = viewModel.initBonusFragment()
                if (bonusFragment !is ExercisesBonusEndScreenFragment) {
                    (activity as AppCompatActivity).replace(
                        bonusFragment,
                        R.id.exerciseBonusLayoutFrameLayout,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right,
                        addToBackStack = false
                    )
                } else {
                    showBonusEndFragment(bonusFragment)
                }
            })
        }
    }

    private fun showBonusEndFragment(bonusEndScreenFragment: ExercisesBonusEndScreenFragment) {
        (activity as AppCompatActivity).navigateTo(
            bonusEndScreenFragment,
            R.id.frameLayout
        )
        (activity as AppCompatActivity).removeFragment(this)
        (activity as AppCompatActivity).supportFragmentManager.findFragmentById(R.id.exerciseBonusLayoutFrameLayout)
            ?.let { (activity as AppCompatActivity).removeFragment(it) }
    }

}