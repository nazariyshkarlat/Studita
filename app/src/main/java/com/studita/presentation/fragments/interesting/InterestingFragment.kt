package com.studita.presentation.fragments.interesting

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.studita.R
import com.studita.presentation.fragments.base.BaseFragment
import com.studita.presentation.view_model.InterestingViewModel
import com.studita.utils.*
import kotlinx.android.synthetic.main.exercise_layout.*
import kotlinx.android.synthetic.main.exercise_toolbar.*
import java.io.IOException

class InterestingFragment : BaseFragment(R.layout.exercise_layout) {

    private var interestingViewModel: InterestingViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        interestingViewModel = activity?.run {
            ViewModelProviders.of(this).get(InterestingViewModel::class.java)
        }

        interestingViewModel?.let { viewModel ->

            viewModel.navigationEvent.observe(viewLifecycleOwner, Observer { pair ->
                when (pair.first) {
                    InterestingViewModel.InterestingNavigationState.NAVIGATE -> {
                        replace(
                            pair.second,
                            R.id.exerciseLayoutFrameLayout,
                            addToBackStack = true
                        )
                    }
                    InterestingViewModel.InterestingNavigationState.ADD -> addFragment(
                        pair.second,
                        R.id.exerciseLayoutFrameLayout,
                        addToBackStack = true
                    )
                    InterestingViewModel.InterestingNavigationState.REPLACE -> replace(
                        pair.second,
                        R.id.exerciseLayoutFrameLayout,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right,
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        addToBackStack = true
                    )
                }
                setBackButtonIcon(pair.second)
            })

            viewModel.showToolbarDividerEvent.observe(viewLifecycleOwner, Observer { show ->
                exerciseLayoutToolbar.background = if(show) ContextCompat.getDrawable(
                    context!!,
                    R.drawable.divider_bottom_drawable,
                ) else null
            })

            viewModel.showButtonDividerEvent.observe(viewLifecycleOwner, Observer { show ->
                exerciseLayoutButtonFrameLayout.background = if (show) ContextCompat.getDrawable(
                    context!!,
                    R.drawable.divider_top_drawable,
                ) else null
            })

            viewModel.progressBarAnimationEvent.observe(viewLifecycleOwner, Observer { pair ->
                val percent = pair.first
                exerciseToolbarProgressBar.animateProgress(
                    toPercent = percent,
                    delay = (if ((exerciseToolbarProgressBar.animation == null) || this.exerciseToolbarProgressBar.animation.hasEnded()) 100L else 0L)
                )
            })

            viewModel.interestingProgress.observe(viewLifecycleOwner, Observer { progress ->
                when (progress) {
                    InterestingViewModel.InterestingState.START_SCREEN -> {
                        exerciseToolbarProgressBar.alpha = 0F
                        exerciseLayoutButton.text = resources.getString(R.string.begin)
                    }
                    InterestingViewModel.InterestingState.STEP -> {
                        if (exerciseToolbarProgressBar.alpha == 0F)
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
                val isLastFragment = viewModel.getProgressBarState().second
                if (!isLastFragment)
                    viewModel.initFragment()
                else
                    activity?.finish()
            }

            exerciseToolbarLeftIcon.setOnClickListener {
                activity?.onBackPressed()
            }

            if (savedInstanceState == null)
                viewModel.initFragment()
            else
                childFragmentManager.findFragmentById(R.id.exerciseLayoutFrameLayout)?.let {
                    setBackButtonIcon(
                        it
                    )
                }
        }
    }

    fun onBackClick(){
        if(childFragmentManager.findFragmentById(R.id.exerciseLayoutFrameLayout) is InterestingStartScreenFragment) {
            activity?.finish()
            return
        }else
            childFragmentManager.popBackStack()

        interestingViewModel?.onBackClick()
        childFragmentManager.executePendingTransactions()
        val currentFragment = childFragmentManager.findFragmentById(R.id.exerciseLayoutFrameLayout)
        if (currentFragment != null) {
            setBackButtonIcon(currentFragment)
        }
    }

    private fun setBackButtonIcon(currentFragment: Fragment){
        if(currentFragment is InterestingStartScreenFragment)
            exerciseToolbarLeftIcon.setImageResource(R.drawable.ic_close)
        else
            exerciseToolbarLeftIcon.setImageResource(R.drawable.ic_arrow_back)
    }

}