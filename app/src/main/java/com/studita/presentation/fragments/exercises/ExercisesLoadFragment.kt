package com.studita.presentation.fragments.exercises

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.studita.R
import com.studita.presentation.view_model.ExercisesViewModel
import com.studita.utils.UserUtils
import com.studita.utils.replace
import kotlinx.android.synthetic.main.exercises_load_layout.*

class ExercisesLoadFragment : LoadFragment() {

    var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesViewModel?.let { viewModel ->


            viewModel.exercisesEvent.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer<Boolean> { done ->
                    if (done) {
                        (activity as AppCompatActivity).replace(
                            ExercisesFragment(),
                            R.id.frameLayout,
                            0,
                            android.R.animator.fade_out,
                            addToBackStack = false
                        )
                    }
                })

            viewModel.loadScreenBadConnectionState.observe(viewLifecycleOwner, Observer {
                if(it)
                    formBadConnectionButton{viewModel.getExercises()}
            })

        }

        if (!UserUtils.isLoggedIn())
            exercisesLoadLayoutTipTextView.text =
                resources.getString(R.string.load_screen_un_logged_text)

    }

}