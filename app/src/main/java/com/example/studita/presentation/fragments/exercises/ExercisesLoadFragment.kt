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
import androidx.lifecycle.lifecycleScope
import com.example.studita.R
import com.example.studita.presentation.fragments.LoadFragment
import com.example.studita.utils.UserUtils
import com.example.studita.utils.replace
import com.example.studita.presentation.view_model.ExercisesViewModel
import com.example.studita.utils.PrefsUtils
import kotlinx.android.synthetic.main.exercises_load_layout.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ExercisesLoadFragment : LoadFragment(){

    var exercisesViewModel: ExercisesViewModel? = null

    private val connectionTimeout = 3000L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesViewModel?.let {viewModel->

            lifecycleScope.launch {
                delay(connectionTimeout)
                formBadConnectionButton(viewModel)
            }

            viewModel.exercisesState.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer<Boolean> { done ->
                    if (done) {
                        (activity as AppCompatActivity).replace(ExercisesFragment(), R.id.frameLayout, 0, android.R.animator.fade_out)
                    }else{
                        formBadConnectionButton(viewModel)
                    }
                })

            viewModel.errorState.observe(viewLifecycleOwner, Observer{ message->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            })
        }

        if(!UserUtils.isLoggedIn())
            exercisesLoadLayoutTipTextView.text = resources.getString(R.string.load_screen_un_logged_text)

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

    private fun formBadConnectionButton(viewModel: ExercisesViewModel){

        exercisesLoadLayoutTipTextView.text = resources.getString(R.string.issues_with_connecting)
        exercisesLoadLayoutButton.visibility = View.VISIBLE
        exercisesLoadLayoutButton.text = resources.getString(R.string.to_offline_mode)
        exercisesLoadLayoutButton.setOnClickListener {
            PrefsUtils.setOfflineMode(true)
            viewModel.getExercises(viewModel.chapterPartNumber)
        }


        exercisesLoadLayoutBottomSection.alpha = 0F

        exercisesLoadLayoutBottomSection.animate().alpha(1F).setDuration(resources.getInteger(R.integer.exercises_load_layout_bottom_section_alpha_anim_duration).toLong()).start()
    }

}