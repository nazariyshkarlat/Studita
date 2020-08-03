package com.example.studita.presentation.fragments.exercises

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.base.NavigatableFragment
import com.example.studita.presentation.view_model.ExercisesViewModel
import com.example.studita.utils.addFragment
import com.example.studita.utils.navigateTo
import com.example.studita.utils.removeFragment
import com.example.studita.utils.replace
import kotlinx.android.synthetic.main.exercise_bonus_layout.*

class ExercisesBonusFragment : NavigatableFragment(R.layout.exercise_bonus_layout){

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

                if(it == 0L){
                    viewModel.endBonusExercises()
                    showBonusEndFragment(viewModel.getExercisesBonusEndFragment())
                    return@Observer
                }

                exerciseBonusLayoutCounterText.text = if (it > 9) it.toString() else "0$it"

                if (it <= 5) {
                    exerciseBonusLayoutCounterIcon.setImageResource(R.drawable.ic_access_time_red)
                    exerciseBonusLayoutCounterText.setTextColor(
                        ContextCompat.getColor(
                            view.context,
                            R.color.red
                        )
                    )
                }

            })

            viewModel.exerciseBonusNavigationState.observe(viewLifecycleOwner, Observer {
                val bonusFragment = viewModel.initBonusFragment()
                if(bonusFragment !is ExercisesBonusEndScreenFragment) {
                    (activity as AppCompatActivity).replace(
                        bonusFragment,
                        R.id.exerciseBonusLayoutFrameLayout,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                    )
                }else{
                    showBonusEndFragment(bonusFragment)
                }
            })
        }
    }

    private fun showBonusEndFragment(bonusEndScreenFragment: ExercisesBonusEndScreenFragment){
        (activity as AppCompatActivity).navigateTo(
            bonusEndScreenFragment,
            R.id.frameLayout
        )
        (activity as AppCompatActivity).removeFragment(this)
        (activity as AppCompatActivity).supportFragmentManager.findFragmentById(R.id.exerciseBonusLayoutFrameLayout)?.let { (activity as AppCompatActivity).removeFragment(it) }
    }

}