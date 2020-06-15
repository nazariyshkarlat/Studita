package com.example.studita.presentation.fragments.exercises

import android.content.Context
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.OneShotPreDrawListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseResponseData
import com.example.studita.utils.*
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.model.*
import com.example.studita.presentation.view_model.ExercisesViewModel
import com.google.android.flexbox.FlexboxLayout
import kotlinx.android.synthetic.main.exercise_bottom_snackbar.*
import kotlinx.android.synthetic.main.exercise_layout.*
import kotlinx.android.synthetic.main.exercise_toolbar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ExercisesFragment : BaseFragment(R.layout.exercise_layout){

    var exercisesViewModel: ExercisesViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesViewModel?.let{viewModel ->

            viewModel.navigationState.observe(viewLifecycleOwner, Observer{ pair->
                when(pair.first){
                    ExercisesViewModel.ExercisesNavigationState.FIRST -> {
                        (activity as AppCompatActivity).navigateTo(
                            pair.second,
                            R.id.exerciseLayoutFrameLayout
                        )
                    }
                    ExercisesViewModel.ExercisesNavigationState.REPLACE -> (activity as AppCompatActivity).replace(pair.second, R.id.exerciseLayoutFrameLayout, R.anim.slide_in_left, R.anim.slide_out_right)
                }
                setButtonText()
            })

            viewModel.snackbarState.observe(viewLifecycleOwner, getSnackbarStateObserver())

            viewModel.buttonEnabledState.observe(viewLifecycleOwner, Observer { enabled ->
                exerciseLayoutButton.isEnabled = enabled
                exerciseLayoutButton.setOnClickListener {
                    exercisesViewModel?.checkExerciseResult()
                }
            })

            viewModel.buttonTextState.observe(viewLifecycleOwner, Observer { text ->
                exerciseLayoutButton.text = text
            })

            viewModel.toolbarDividerState.observe(viewLifecycleOwner, Observer { show ->
                exerciseLayoutToolbar.background =  if(show) resources.getDrawable(R.drawable.divider_bottom_drawable, exerciseLayoutToolbar.context.theme) else null
            })

            viewModel.buttonDividerState.observe(viewLifecycleOwner, Observer { show ->
                exerciseLayoutButtonFrameLayout.background =  if(show) resources.getDrawable(R.drawable.divider_top_drawable, exerciseLayoutToolbar.context.theme) else null
            })

            viewModel.barsState.observe(viewLifecycleOwner, Observer { show->
                exerciseLayoutButtonViewTransparent.visibility = if (show) View.VISIBLE else View.GONE
                exerciseToolbarViewTransparent.visibility = if (show) View.VISIBLE else View.GONE
            })

            viewModel.progressState.observe(viewLifecycleOwner, getProgressStateObserver(viewModel))

            viewModel.exercisesProgress.observe(viewLifecycleOwner, Observer { state ->
                when (state) {
                    ExercisesViewModel.ExercisesState.START_SCREEN -> {
                        exerciseToolbarProgressBar.alpha = 0F
                        exerciseLayoutButton.setOnClickListener {
                            if(viewModel.exercisesResponseData.exercisesDescription != null)
                                viewModel.setExercisesProgress(ExercisesViewModel.ExercisesState.DESCRIPTION)
                            else {
                                viewModel.setExercisesProgress(ExercisesViewModel.ExercisesState.EXERCISES)
                                viewModel.initFragment()
                            }
                        }
                    }
                    ExercisesViewModel.ExercisesState.DESCRIPTION -> {
                        if(savedInstanceState == null)
                            (activity as AppCompatActivity).navigateTo(viewModel.getDescriptionFragment(), R.id.exerciseLayoutFrameLayout)
                        formDescriptionView()
                    }
                    else -> {
                        formExercisesView()
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                            delay(resources.getInteger(R.integer.navigatable_fragment_anim_duration).toLong())
                            viewModel.showBars(false)
                        }
                    }
                }
            })

            viewModel.showBadConnectionDialogAlertFragmentState.observe(viewLifecycleOwner, Observer {show->
                if(show) {
                    fragmentManager?.let {
                        viewModel.snackbarState.removeObservers(viewLifecycleOwner)
                        viewModel.progressState.removeObservers(viewLifecycleOwner)
                        val dialogFragment = ExercisesBadConnectionDialogAlertFragment()

                        dialogFragment.show(
                            it,
                            null
                        )

                        it.executePendingTransactions()

                        dialogFragment.dialog?.setOnDismissListener {
                            viewModel.progressState.observe(viewLifecycleOwner, getProgressStateObserver(viewModel))
                            viewModel.snackbarState.observe(viewLifecycleOwner, getSnackbarStateObserver())
                        }
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
        if(hasFocus) {
            if (exercisesViewModel?.secondsCounterIsStopped() == true)
                exercisesViewModel?.startSecondsCounter()
        }else {
            exercisesViewModel?.stopSecondsCounter()
        }
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
            val exercisesResponseData = it.second
            formSnackBarView(
                exercisesResponseData.toUiModel(exerciseBottomSnackbarLinearLayout.context)
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


    private fun formSnackBarView(responseData: ExerciseResponseUiModel) {
        if (responseData.exerciseResult) {
            formTrueSnackbarAnswerView()
        } else {
            formFalseAnswerSnackbarView(responseData)
        }
    }

    private fun formTrueSnackbarAnswerView(){
        exerciseBottomSnackbarTitle.text = resources.getString(R.string.true_answer)
        exerciseLayoutSnackbar.setBackgroundColor(
            ContextCompat.getColor(exerciseBottomSnackbarLinearLayout.context, R.color.green)
        )
        exerciseBottomSnackbarFlexbox.visibility = View.GONE
        exerciseBottomSnackbarSubtitle.visibility = View.GONE
    }

    private fun formFalseAnswerSnackbarView(responseData: ExerciseResponseUiModel){
        exerciseLayoutSnackbar.setBackgroundColor(
            ContextCompat.getColor(
                exerciseBottomSnackbarLinearLayout.context,
                R.color.red
            )
        )
        formFalseAnswerSnackbarTitle()
        formFalseAnswerSnackbarSubtitle(responseData)
    }

    private fun formSnackbarFlexboxSubtitle(shapeUiModel: ExerciseShapeUiModel){
        exerciseBottomSnackbarSubtitle.visibility = View.GONE
        exerciseBottomSnackbarFlexbox.visibility = View.VISIBLE
        for(i in 0 until shapeUiModel.count) {
            val shapeView = View(exerciseBottomSnackbarFlexbox.context)
            val params = FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.WRAP_CONTENT, FlexboxLayout.LayoutParams.WRAP_CONTENT)
            params.height = 20.dpToPx()
            params.width = 20.dpToPx()
            shapeView.layoutParams = params
            shapeView.background =  shapeUiModel.shape
            exerciseBottomSnackbarFlexbox.addView(shapeView)
        }
    }

    private fun formFalseAnswerSnackbarTitle(){
        if(exercisesViewModel?.exerciseUiModel is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType9UiModel)
            exerciseBottomSnackbarTitle.text = resources.getString(R.string.exercise_type_9_false_snackbar_title)
        else
            exerciseBottomSnackbarTitle.text = resources.getString(R.string.false_answer)
    }

    private fun formFalseAnswerSnackbarSubtitle(responseData: ExerciseResponseUiModel){
        responseData.description?.let {description->
            if (description.descriptionContent is ExerciseResponseDescriptionContentUiModel.DescriptionContentString) {
                exerciseBottomSnackbarSubtitle.visibility = View.VISIBLE
                exerciseBottomSnackbarFlexbox.visibility = View.GONE
                val descriptionSubtitle = description.descriptionContent.string
                exerciseBottomSnackbarSubtitle.text = descriptionSubtitle
            }else if(description.descriptionContent is ExerciseResponseDescriptionContentUiModel.DescriptionContentArray){
                val count = description.descriptionContent.shape.count
                if(count == 0){
                    formSnackbarSubtitleForNullShapes()
                }else{
                    exerciseBottomSnackbarFlexbox.removeAllViews()
                    formSnackbarFlexboxSubtitle(description.descriptionContent.shape)
                }
            }
        }
    }

    private fun formSnackbarSubtitleForNullShapes(){
        exerciseBottomSnackbarSubtitle.visibility = View.VISIBLE
        exerciseBottomSnackbarFlexbox.visibility = View.GONE
        exerciseBottomSnackbarSubtitle.text = resources.getString(R.string.exercise_shape_0_rect)
    }

    private fun changeButton(onSnackbarShow: Boolean, animate: Boolean){
        val context = exerciseLayoutButton.context
        val drawable = exerciseLayoutButton.background as TransitionDrawable
        if (onSnackbarShow) {
            exerciseLayoutButton.setTextColor(
                ThemeUtils.getAccentColor(context)
            )
            drawable.startTransition(if(animate) resources.getInteger(R.integer.button_transition_duration) else 0)
            exerciseLayoutButton.text = resources.getString(R.string.next)
            exerciseLayoutButton.setOnClickListener {
                hideSnackBar()
                changeButton(false, animate)
                exercisesViewModel?.initFragment()
            }
        } else {
            exerciseLayoutButton.setTextColor(
                ContextCompat.getColorStateList(
                    exerciseLayoutButton.context,
                    R.color.button_accent_8_text_color
                )
            )
            drawable.reverseTransition(resources.getInteger(R.integer.button_transition_duration))
            exerciseLayoutButton.text = resources.getString(R.string.check)
        }
    }

    private fun formDescriptionView(){
        exerciseToolbarProgressBar.alpha = 0F
        exerciseLayoutButton.text = resources.getString(R.string.continue_string)
        exerciseLayoutButton.setOnClickListener{
            exercisesViewModel?.setExercisesProgress(ExercisesViewModel.ExercisesState.EXERCISES)
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
        exercisesViewModel?.apply {
            setButtonText(
                when (val exerciseUiModel = this.exerciseUiModel) {
                    is ExerciseUiModel.ExerciseUiModelExercise -> resources.getString(R.string.check)
                    is ExerciseUiModel.ExerciseUiModelScreen -> {
                        if (exerciseUiModel.exerciseNumber == null)
                            resources.getString(R.string.continue_string)
                        else
                            resources.getString(R.string.next)
                    }
                }
            )
        }
    }

    private fun getProgressStateObserver(exercisesViewModel: ExercisesViewModel) =
        Observer<Pair<Float, Boolean>> { pair ->
            val progress = pair.first
            val last = pair.second
            exerciseToolbarProgressBar.animateProgress(toPercent = progress, delay = (if((exerciseToolbarProgressBar.animation == null) || this.exerciseToolbarProgressBar.animation.hasEnded()) 100L else 0L), onAnimEnd = {
                if(last){
                    exerciseLayoutButton.setOnClickListener {  }
                    exercisesViewModel.saveUserDataState.observe(viewLifecycleOwner, Observer { pair->
                        val saved = pair.first
                        if(saved){
                            (activity as AppCompatActivity).navigateTo(exercisesViewModel.getExercisesEndFragment(pair.second), R.id.frameLayout)
                        }
                    })
                }
            })
        }

    private fun getSnackbarStateObserver() =
        Observer<Pair<ExerciseUiModel, ExerciseResponseData>?> { response ->
            showSnackbar(response)
        }

}