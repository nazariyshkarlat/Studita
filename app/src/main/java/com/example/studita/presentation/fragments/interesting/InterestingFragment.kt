package com.example.studita.presentation.fragments.interesting

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.utils.addFragment
import com.example.studita.utils.animateProgress
import com.example.studita.utils.navigateTo
import com.example.studita.utils.replace
import com.example.studita.presentation.view_model.InterestingViewModel
import kotlinx.android.synthetic.main.exercise_layout.*
import kotlinx.android.synthetic.main.exercise_toolbar.*
import java.io.IOException

class InterestingFragment : BaseFragment(R.layout.exercise_layout){

    private var interestingViewModel: InterestingViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        interestingViewModel = activity?.run {
            ViewModelProviders.of(this).get(InterestingViewModel::class.java)
        }

        interestingViewModel?.let { viewModel ->

            viewModel.navigationState.observe(viewLifecycleOwner, Observer { pair ->
                when (pair.first) {
                    InterestingViewModel.InterestingNavigationState.NAVIGATE -> {
                        (activity as AppCompatActivity).navigateTo(
                            pair.second,
                            R.id.exerciseLayoutFrameLayout
                        )
                    }
                    InterestingViewModel.InterestingNavigationState.ADD -> (activity as AppCompatActivity).addFragment(
                        pair.second,
                        R.id.exerciseLayoutFrameLayout
                    )
                    InterestingViewModel.InterestingNavigationState.REPLACE -> (activity as AppCompatActivity).replace(
                        pair.second,
                        R.id.exerciseLayoutFrameLayout,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                    )
                }
            })

            viewModel.toolbarDividerState.observe(viewLifecycleOwner, Observer { show ->
                exerciseLayoutToolbar.background = if (show) resources.getDrawable(
                    R.drawable.divider_bottom_drawable,
                    exerciseLayoutToolbar.context.theme
                ) else null
            })

            viewModel.buttonDividerState.observe(viewLifecycleOwner, Observer { show ->
                exerciseLayoutButtonFrameLayout.background = if (show) resources.getDrawable(
                    R.drawable.divider_top_drawable,
                    exerciseLayoutToolbar.context.theme
                ) else null
            })

            viewModel.progressBarState.observe(viewLifecycleOwner, Observer { pair ->
                val percent = pair.first
                val last = pair.second
                exerciseToolbarProgressBar.animateProgress(toPercent = percent, delay = (if((exerciseToolbarProgressBar.animation == null) || this.exerciseToolbarProgressBar.animation.hasEnded()) 100L else 0L))
                if(last)
                    exerciseLayoutButton.setOnClickListener {
                        activity?.finish()
                    }
            })

            viewModel.interestingProgress.observe(viewLifecycleOwner, Observer { progress ->
                when (progress) {
                    InterestingViewModel.InterestingState.START_SCREEN -> {
                        exerciseToolbarProgressBar.alpha = 0F
                        exerciseLayoutButton.text = resources.getString(R.string.begin)
                    }
                    InterestingViewModel.InterestingState.STEP -> {
                        if(exerciseToolbarProgressBar.alpha == 0F)
                            exerciseToolbarProgressBar.animate().alpha(1F).start()
                        exerciseLayoutButton.text = resources.getString(R.string.next)
                    }
                    InterestingViewModel.InterestingState.EXPLANATION -> {
                        exerciseLayoutButton.text = resources.getString(R.string.complete)
                    }
                    else -> throw IOException("unknown interesting progress")
                }
            })

            exerciseLayoutButton.setOnClickListener {
                viewModel.initFragment()
            }

            exerciseToolbarCloseButton.setOnClickListener {
                activity?.onBackPressed()
            }

            if(savedInstanceState == null)
                viewModel.initFragment()
        }
    }

}