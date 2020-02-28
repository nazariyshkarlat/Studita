package com.example.studita.presentation.fragments

import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.OneShotPreDrawListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseResponseData
import com.example.studita.presentation.extensions.addFragment
import com.example.studita.presentation.extensions.animateProgress
import com.example.studita.presentation.extensions.navigateTo
import com.example.studita.presentation.extensions.replaceWithAnim
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import kotlinx.android.synthetic.main.exercise_bottom_snackbar.*
import kotlinx.android.synthetic.main.exercise_layout.*
import kotlinx.android.synthetic.main.exercise_layout.view.*
import kotlinx.android.synthetic.main.exercise_toolbar.*


class ExercisesFragment : BaseFragment(R.layout.exercise_layout){

    private val buttonChangeDelay = 80L
    var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesViewModel?.let{viewModel ->
            viewModel.navigationState.observe(viewLifecycleOwner, Observer{ fragment->
                when(fragment.first){
                    ExercisesViewModel.ExercisesNavigationState.FIRST -> (activity as AppCompatActivity).addFragment(fragment.second, R.id.exerciseLayoutFrameLayout)
                    ExercisesViewModel.ExercisesNavigationState.REPLACE -> (activity as AppCompatActivity).replaceWithAnim(fragment.second, R.id.exerciseLayoutFrameLayout, R.animator.slide_in_left, R.animator.slide_out_right)
                }
                exerciseLayoutButton.setOnClickListener {
                    viewModel.checkExerciseResult()
                }
            })

            viewModel.snackbarState.observe(viewLifecycleOwner, Observer { response ->
                showSnackbar(response)
            })

            viewModel.exercisesButtonState.observe(viewLifecycleOwner, Observer { enabled ->
                exerciseLayoutButton.isEnabled = enabled
            })

            viewModel.progressBarState.observe(viewLifecycleOwner, Observer { pair ->
                val progress = pair.first
                val last = pair.second
                exerciseToolbarProgressBar.animateProgress(percent = progress, onAnimEnd = {
                    if(last){
                        (activity as AppCompatActivity).navigateTo(viewModel.getExercisesEndFragment(), R.id.frameLayout)
                    }
                })
            })

            if(savedInstanceState == null)
                viewModel.initFragment()

            exerciseLayoutButton.setOnClickListener {
                viewModel.checkExerciseResult()
            }
        }

        exerciseToolbarCloseButton.setOnClickListener {
            (activity as AppCompatActivity).onBackPressed()
        }

        exerciseBottomSnackbarIcon.setOnClickListener {  }

        setSnackbarTranslationY()
    }

    fun onWindowFocusChanged(hasFocus: Boolean){
        if(hasFocus)
            exercisesViewModel?.startSecondsCounter()
        else
            exercisesViewModel?.stopSecondsCounter()
    }

    fun onBackClick(){
        val fragment = ExercisesCloseDialogAlertFragment()
        activity?.supportFragmentManager?.let {
            fragment.show(it, null)
            fragment.dialog?.setOnShowListener {
                exercisesViewModel?.stopSecondsCounter()
            }
        }
    }

    private fun showSnackbar(data: Pair<ExerciseUiModel, ExerciseResponseData>){

        val exercisesResponseData = data.second
        val exerciseUiModel = data.first
        formSnackBarView(exerciseUiModel, exercisesResponseData.exerciseResult, exercisesResponseData.description)

        exerciseLayoutSnackbar
        exerciseLayoutSnackbar.animate().translationY(0F).setDuration(resources.getInteger(R.integer.snackbar_anim_duration).toLong()).setInterpolator(FastOutSlowInInterpolator()).start()

        changeButton(true)
    }

    private fun hideSnackBar(){
        if (exerciseLayoutFrameLayout.translationY != 0F)
            exerciseLayoutFrameLayout.animate().translationY(0F).setDuration(resources.getInteger(R.integer.snackbar_anim_duration).toLong()).setInterpolator(
                FastOutSlowInInterpolator()
            ).start()

        exerciseLayoutSnackbar.animate().translationY(exerciseLayoutSnackbar.height.toFloat())
            .setInterpolator(
                FastOutSlowInInterpolator()
            ).start()
    }

    private fun setSnackbarTranslationY(){
        OneShotPreDrawListener.add(exerciseLayoutButton){
            val buttonParams =
                exerciseLayoutButton.layoutParams as ConstraintLayout.LayoutParams
            val snackbarLinearLayoutBottomMargin =
                exerciseLayoutButton.height + buttonParams.bottomMargin
            val snackBarParams =
                exerciseBottomSnackbarLinearLayout.layoutParams as RelativeLayout.LayoutParams
            snackBarParams.bottomMargin = snackbarLinearLayoutBottomMargin
            exerciseBottomSnackbarLinearLayout.layoutParams = snackBarParams
            exerciseLayoutSnackbar.translationY =
                exerciseLayoutSnackbar.height + snackbarLinearLayoutBottomMargin.toFloat()
            exerciseLayoutSnackbar.visibility = View.VISIBLE
        }
    }


    private fun formSnackBarView(exerciseUiModel: ExerciseUiModel, answerIsCorrect: Boolean, subtitleText: String?) {
        context?.let {
            if (answerIsCorrect) {
                exerciseBottomSnackbarTitle.text = resources.getString(R.string.true_answer)
                exerciseLayoutSnackbar.setBackgroundColor(
                    ContextCompat.getColor(it, R.color.green)
                )
                exerciseBottomSnackbarSubtitle.visibility = View.GONE
            } else {
                if(exerciseUiModel is ExerciseUiModel.ExerciseUi6)
                    exerciseBottomSnackbarTitle.text = resources.getString(R.string.exercise_type_3_false_snackbar_title)
                else
                    exerciseBottomSnackbarTitle.text = resources.getString(R.string.false_answer)
                exerciseBottomSnackbarSubtitle.text = when (subtitleText) {
                    "true" ->  resources.getString(R.string.true_variant)
                    "false" ->  resources.getString(R.string.false_variant)
                    "division by 0" -> resources.getString(R.string.division_by_0)
                    else ->  subtitleText
                }
                exerciseLayoutSnackbar.setBackgroundColor(ContextCompat.getColor(it, R.color.red))
                exerciseBottomSnackbarSubtitle.visibility = View.VISIBLE
            }
        }
    }

    private fun changeButton(onSnackbarShow: Boolean){
        view?.let {
            Handler().postDelayed({
                val drawable = it.exerciseLayoutButton.background as TransitionDrawable
                if (onSnackbarShow) {
                    it.exerciseLayoutButton.setTextColor(
                        ContextCompat.getColor(it.context, R.color.blue))
                    drawable.startTransition(resources.getInteger(R.integer.button_transition_duration))
                    it.exerciseLayoutButton.text = resources.getString(R.string.next)
                    it.exerciseLayoutButton.setOnClickListener {
                        hideSnackBar()
                        exerciseLayoutButton.isEnabled = false
                        changeButton(false)
                        exercisesViewModel?.initFragment()
                    }
                } else {
                    it.exerciseLayoutButton.setTextColor(
                        ContextCompat.getColorStateList(
                            exerciseLayoutButton.context,
                            R.color.blue_button_8_text_color
                        )
                    )
                    drawable.resetTransition()
                    it.exerciseLayoutButton.text = resources.getString(R.string.check)
                }
            }, buttonChangeDelay)
        }
    }

}