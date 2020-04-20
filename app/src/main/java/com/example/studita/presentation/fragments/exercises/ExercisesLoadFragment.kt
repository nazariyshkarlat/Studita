package com.example.studita.presentation.fragments.exercises

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.LoadFragment
import com.example.studita.presentation.utils.replace
import com.example.studita.presentation.view_model.ExercisesViewModel

class ExercisesLoadFragment : LoadFragment(){

    var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesViewModel?.let {
            it.exercisesState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer<Boolean> { done ->
                    if (done) {
                        (activity as AppCompatActivity).replace(ExercisesFragment(), R.id.frameLayout, 0, android.R.animator.fade_out)
                    }
                })

            it.errorState.observe(viewLifecycleOwner, Observer{ message->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            })
        }

    }

    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? {
        return if(!enter){
            val animator = AnimatorInflater.loadAnimator(context, nextAnim)
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    exercisesViewModel?.startSecondsCounter()
                }
            })
            animator
        }else
            null
    }

}