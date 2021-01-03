package com.studita.presentation.fragments.exercises

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.studita.R
import com.studita.presentation.fragments.base.NavigatableFragment
import com.studita.presentation.view_model.ExercisesEndFragmentViewModel
import com.studita.presentation.view_model.ExercisesViewModel
import com.studita.utils.addFragment
import com.studita.utils.navigateTo
import kotlinx.android.synthetic.main.exercises_end_layout.*

class ExercisesEndFragment : NavigatableFragment(R.layout.exercises_end_layout) {

    init {
        isLongAnim = true
    }

    var exercisesViewModel: ExercisesViewModel? = null
    private var exercisesEndFragmentViewModel: ExercisesEndFragmentViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesEndFragmentViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesEndFragmentViewModel::class.java)
        }

        exercisesViewModel?.let {
            it.endButtonEvent.observe(viewLifecycleOwner, Observer { show ->
                exercisesEndLayoutTextButton.visibility = if (show) View.VISIBLE else View.GONE
            })
        }

        exercisesEndFragmentViewModel?.let {
            it.buttonsLayoutDividerPadding.observe(viewLifecycleOwner, Observer { pair ->
                exercisesEndLayoutButtonsLayout.background =
                    context?.let { it1 -> ContextCompat.getDrawable(it1, pair.first) }
                exercisesEndLayoutButtonsLayout.setPadding(
                    exercisesEndLayoutButtonsLayout.paddingLeft,
                    pair.second,
                    exercisesEndLayoutButtonsLayout.paddingRight,
                    exercisesEndLayoutButtonsLayout.paddingBottom
                )
            })
        }

        if (savedInstanceState == null) {
            exercisesEndFragmentViewModel?.getExercisesResultFragment(this)?.let {
                (activity as AppCompatActivity).addFragment(
                    it,
                    R.id.exercisesEndLayoutFrameLayout
                )
            }
        }
        exercisesEndLayoutButton.setOnClickListener {
            (activity as AppCompatActivity).finish()
        }

        exercisesEndLayoutTextButton.setOnClickListener {
            exercisesEndFragmentViewModel?.getExercisesDetailedStatFragment(this)?.let { it1 ->
                (activity as AppCompatActivity).navigateTo(
                    it1, R.id.exercisesEndLayoutFrameLayout
                )
            }
        }
    }

}