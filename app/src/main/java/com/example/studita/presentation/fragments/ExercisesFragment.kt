package com.example.studita.presentation.fragments

import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.OneShotPreDrawListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.studita.R
import com.example.studita.data.entity.exercise.ExerciseResponseDescriptionContent
import com.example.studita.domain.entity.exercise.ExerciseResponseData
import com.example.studita.domain.entity.exercise.ExerciseResponseDescriptionContentData
import com.example.studita.presentation.extensions.*
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.view_model.ExercisesViewModel
import com.google.android.flexbox.FlexboxLayout
import kotlinx.android.synthetic.main.exercise_bottom_snackbar.*
import kotlinx.android.synthetic.main.exercise_layout.*
import kotlinx.android.synthetic.main.exercise_layout.view.*
import kotlinx.android.synthetic.main.exercise_screen_type_1.*
import kotlinx.android.synthetic.main.exercise_toolbar.*


class ExercisesFragment : BaseFragment(R.layout.exercise_layout){

    var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesViewModel?.let{viewModel ->
            viewModel.navigationState.observe(viewLifecycleOwner, Observer{ fragment->
                when(fragment.first){
                    ExercisesViewModel.ExercisesNavigationState.FIRST -> {
                        exercisesViewModel?.exercisesProgress?.value =
                            ExercisesViewModel.ExercisesState.EXERCISES
                        (activity as AppCompatActivity).navigateTo(
                            fragment.second,
                            R.id.exerciseLayoutFrameLayout
                        )
                    }
                    ExercisesViewModel.ExercisesNavigationState.REPLACE -> (activity as AppCompatActivity).replaceWithAnim(fragment.second, R.id.exerciseLayoutFrameLayout, R.animator.slide_in_left, R.animator.slide_out_right)
                }
                setButtonText()
            })

            viewModel.snackbarState.observe(viewLifecycleOwner, Observer { response ->
                showSnackbar(response)
            })

            viewModel.exercisesButtonEnabledState.observe(viewLifecycleOwner, Observer { enabled ->
                exerciseLayoutButton.isEnabled = enabled
                exerciseLayoutButton.setOnClickListener {
                    exercisesViewModel?.checkExerciseResult()
                }
            })

            viewModel.exercisesButtonTextState.observe(viewLifecycleOwner, Observer { text ->
                exerciseLayoutButton.text = text
            })

            viewModel.toolbarDividerState.observe(viewLifecycleOwner, Observer { show ->
                exerciseLayoutToolbar.background =  if(show) resources.getDrawable(R.drawable.divider_bottom_drawable, exerciseLayoutToolbar.context.theme) else null
            })

            viewModel.buttonDividerState.observe(viewLifecycleOwner, Observer { show ->
                exerciseLayoutButtonFrameLayout.background =  if(show) resources.getDrawable(R.drawable.divider_top_drawable, exerciseLayoutToolbar.context.theme) else null
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

            viewModel.exercisesProgress.observe(viewLifecycleOwner, Observer { state ->
                when (state) {
                    ExercisesViewModel.ExercisesState.START_SCREEN -> {
                        exerciseToolbarProgressBar.alpha = 0F
                        exerciseLayoutButton.setOnClickListener {
                            viewModel.exercisesProgress.value = ExercisesViewModel.ExercisesState.DESCRIPTION
                        }
                    }
                    ExercisesViewModel.ExercisesState.DESCRIPTION -> {
                        formDescriptionView()
                    }
                    else -> {
                        formExercisesView()
                    }
                }
            })
        }

        if(savedInstanceState == null) {
            (activity as AppCompatActivity).addFragment(
                ExercisesStartScreenFragment(),
                R.id.exerciseLayoutFrameLayout
            )
        }else{
            if (exercisesViewModel?.answered?.value == true) {
                OneShotPreDrawListener.add(exerciseLayoutSnackbar){
                    setSnackbarTranslationY()
                    showSnackbar(exercisesViewModel?.snackbarState?.value, animate = false)
                }
            }
        }

        exerciseToolbarCloseButton.setOnClickListener {
            (activity as AppCompatActivity).onBackPressed()
        }

        exerciseBottomSnackbarIcon.setOnClickListener {  }
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

    private fun showSnackbar(data: Pair<ExerciseUiModel, ExerciseResponseData>?, animate: Boolean = true) {

        data?.let {
            val exercisesResponseData = data.second
            formSnackBarView(
                exercisesResponseData
            )

            setSnackbarTranslationY()

            if (animate) {
                exerciseLayoutSnackbar.animate().translationY(0F)
                    .setDuration(resources.getInteger(R.integer.snackbar_anim_duration).toLong())
                    .setInterpolator(FastOutSlowInInterpolator()).start()
            } else {
                exerciseLayoutSnackbar.translationY = 0F
            }

            changeButton(true, animate)
        }
    }

    private fun hideSnackBar(){
        exerciseLayoutSnackbar.animate().translationY(exerciseLayoutSnackbar.height.toFloat())
            .setInterpolator(
                FastOutSlowInInterpolator()
            ).start()
    }

    private fun setSnackbarTranslationY(){
        val buttonParams =
            exerciseLayoutButton.layoutParams as FrameLayout.LayoutParams
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


    private fun formSnackBarView(responseData: ExerciseResponseData) {
        context?.let {
            if (responseData.exerciseResult) {
                exerciseBottomSnackbarTitle.text = resources.getString(R.string.true_answer)
                exerciseLayoutSnackbar.setBackgroundColor(
                    ContextCompat.getColor(it, R.color.green)
                )
                exerciseBottomSnackbarFlexbox.visibility = View.GONE
                exerciseBottomSnackbarSubtitle.visibility = View.GONE
            } else {
                responseData.description?.let {description->
                    exerciseBottomSnackbarTitle.text = resources.getString(R.string.false_answer)
                    exerciseLayoutSnackbar.setBackgroundColor(
                        ContextCompat.getColor(
                            it,
                            R.color.red
                        )
                    )
                    if (description.descriptionContent is ExerciseResponseDescriptionContentData.DescriptionContentString) {
                        exerciseBottomSnackbarSubtitle.visibility = View.VISIBLE
                        exerciseBottomSnackbarFlexbox.visibility = View.GONE
                        exerciseBottomSnackbarSubtitle.text = (description.descriptionContent as ExerciseResponseDescriptionContentData.DescriptionContentString).descriptionContent
                    }else if(description.descriptionContent is ExerciseResponseDescriptionContentData.DescriptionContentArray){
                        val count = (description.descriptionContent as ExerciseResponseDescriptionContentData.DescriptionContentArray).descriptionContent.count
                        if(count == 0){
                            exerciseBottomSnackbarSubtitle.visibility = View.VISIBLE
                            exerciseBottomSnackbarFlexbox.visibility = View.GONE
                            exerciseBottomSnackbarSubtitle.text = resources.getString(R.string.exercise_shape_0_rect)
                        }else{
                            exerciseBottomSnackbarFlexbox.removeAllViews()
                            exerciseBottomSnackbarSubtitle.visibility = View.GONE
                            exerciseBottomSnackbarFlexbox.visibility = View.VISIBLE
                            for(i in 0 until count) {
                                val shapeView = View(exerciseBottomSnackbarFlexbox.context)
                                val params = FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT)
                                params.height = 20.dpToPx()
                                params.width = 20.dpToPx()
                                shapeView.layoutParams = params
                                shapeView.background =  ContextCompat.getDrawable(exerciseBottomSnackbarFlexbox.context, R.drawable.exercise_rectangle_white)
                                exerciseBottomSnackbarFlexbox.addView(shapeView)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun changeButton(onSnackbarShow: Boolean, animate: Boolean){
        view?.let {
            val drawable = it.exerciseLayoutButton.background as TransitionDrawable
            if (onSnackbarShow) {
                it.exerciseLayoutButton.setTextColor(
                    ContextCompat.getColor(it.context, R.color.blue)
                )
                drawable.startTransition(if(animate) resources.getInteger(R.integer.button_transition_duration) else 0)
                it.exerciseLayoutButton.text = resources.getString(R.string.next)
                it.exerciseLayoutButton.setOnClickListener {
                    hideSnackBar()
                    changeButton(false, animate)
                    exercisesViewModel?.initFragment()
                }
            } else {
                it.exerciseLayoutButton.setTextColor(
                    ContextCompat.getColorStateList(
                        exerciseLayoutButton.context,
                        R.color.blue_button_8_text_color
                    )
                )
                drawable.reverseTransition(resources.getInteger(R.integer.button_transition_duration))
                it.exerciseLayoutButton.text = resources.getString(R.string.check)
            }
        }
    }

    private fun formDescriptionView(){
        exerciseToolbarProgressBar.alpha = 0F
        val descriptionFragment = ExercisesDescriptionFragment()
        (activity as AppCompatActivity).navigateTo(descriptionFragment, R.id.exerciseLayoutFrameLayout)
        exerciseLayoutButton.text = resources.getString(R.string.continue_string)
        exerciseLayoutButton.setOnClickListener{
            exercisesViewModel?.initFragment()
        }
    }

    private fun formExercisesView(){
        exerciseToolbarProgressBar.animate().alpha(1F).start()
        exerciseLayoutButtonFrameLayout.background = null
        exerciseLayoutButton.setOnClickListener {
            exercisesViewModel?.checkExerciseResult()
        }
    }

    private fun setButtonText(){
        exercisesViewModel?.exercisesButtonTextState?.value =  if(exercisesViewModel?.exerciseUiModel is ExerciseUiModel.ExerciseUiModelExercise)
            resources.getString(R.string.check)
        else
            resources.getString(R.string.continue_string)
    }

}