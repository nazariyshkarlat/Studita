package com.example.studita.presentation.fragments.exercises

import android.content.DialogInterface
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.OneShotPreDrawListener
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseData
import com.example.studita.domain.entity.exercise.ExerciseResponseData
import com.example.studita.presentation.activities.ExercisesActivity
import com.example.studita.presentation.fragments.base.BaseFragment
import com.example.studita.presentation.fragments.dialog_alerts.ExercisesBadConnectionDialogAlertFragment
import com.example.studita.presentation.fragments.exercises.description.ExercisesDescriptionFragment
import com.example.studita.presentation.listeners.OnSingleClickListener.Companion.setOnSingleClickListener
import com.example.studita.presentation.listeners.OnSwipeTouchListener
import com.example.studita.presentation.model.*
import com.example.studita.presentation.view_model.ExercisesViewModel
import com.example.studita.presentation.views.SquareView
import com.example.studita.utils.*
import com.google.android.flexbox.FlexboxLayout
import kotlinx.android.synthetic.main.chapter_part_two_help_layout.*
import kotlinx.android.synthetic.main.chapter_part_two_help_layout.view.*
import kotlinx.android.synthetic.main.exercise_bottom_snackbar.*
import kotlinx.android.synthetic.main.exercise_layout.*
import kotlinx.android.synthetic.main.exercise_toolbar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException


class ExercisesFragment : BaseFragment(R.layout.exercise_layout), DialogInterface.OnDismissListener,
    ExercisesActivity.DispatchTouchEvent {

    var exercisesViewModel: ExercisesViewModel? = null
    var snackbarTranslationY = 0F

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesViewModel?.let { viewModel ->

            viewModel.navigationState.observe(viewLifecycleOwner, Observer { pair ->

                when (pair.first) {
                    ExercisesViewModel.ExercisesNavigationState.FIRST -> {

                        refreshExercisesView()

                        (activity as AppCompatActivity).navigateTo(
                            pair.second,
                            R.id.exerciseLayoutFrameLayout
                        )
                    }
                    ExercisesViewModel.ExercisesNavigationState.REPLACE -> {

                        Handler().postDelayed({
                            refreshExercisesView()
                        }, 300)

                        (activity as AppCompatActivity).replace(
                            pair.second,
                            R.id.exerciseLayoutFrameLayout,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                        )
                    }
                    ExercisesViewModel.ExercisesNavigationState.NAVIGATE -> {
                        (activity as AppCompatActivity).navigateTo(
                            pair.second,
                            R.id.frameLayout
                        )
                        (activity as AppCompatActivity).supportFragmentManager.findFragmentById(R.id.exerciseLayoutFrameLayout)
                            ?.let { (activity as AppCompatActivity).removeFragment(it) }
                        (activity as AppCompatActivity).removeFragment(this)
                    }
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

            viewModel.toolbarProgressBarAnimEvent.value?.let {
                viewModel.setProgressBarVisibility(it)
            }

            viewModel.buttonTextState.observe(viewLifecycleOwner, Observer { text ->
                exerciseLayoutButton.text = text
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

            viewModel.transparentLayoutsAreVisibleState.observe(
                viewLifecycleOwner,
                Observer { show ->
                    exerciseLayoutButtonViewTransparent.visibility =
                        if (show) View.VISIBLE else View.GONE
                    exerciseToolbarViewTransparent.visibility =
                        if (show) View.VISIBLE else View.GONE
                })

            viewModel.toolbarProgressBarAnimEvent.observe(
                viewLifecycleOwner,
                Observer { animateShow ->
                    exerciseToolbarProgressBar.animate().alpha(if (animateShow) 1F else 0F).start()
                })

            viewModel.progressBarVisibleState.observe(viewLifecycleOwner, Observer { isVisible ->
                exerciseToolbarProgressBar.alpha = if (isVisible) 1F else 0F
            })

            viewModel.progressState.observe(viewLifecycleOwner, getProgressStateObserver(viewModel))

            viewModel.exercisesProgress.observe(viewLifecycleOwner, Observer { state ->
                when (state) {
                    ExercisesViewModel.ExercisesState.START_SCREEN -> {
                        exerciseLayoutButton.setOnClickListener {
                            if (viewModel.exercisesResponseData.exercisesDescription != null) {
                                viewModel.setExercisesProgress(ExercisesViewModel.ExercisesState.DESCRIPTION)
                            } else {
                                onExerciseButtonStartExercisesClick()
                            }
                        }
                    }
                    ExercisesViewModel.ExercisesState.DESCRIPTION -> {
                        if (activity?.supportFragmentManager?.findFragmentById(R.id.exerciseLayoutFrameLayout) !is ExercisesDescriptionFragment)
                            (activity as AppCompatActivity).navigateTo(
                                viewModel.getDescriptionFragment(),
                                R.id.exerciseLayoutFrameLayout
                            )
                        formDescriptionView()
                    }
                    ExercisesViewModel.ExercisesState.EXERCISES -> {
                        formExercisesView()
                    }
                    ExercisesViewModel.ExercisesState.BONUS_SCREEN -> {
                        formBonusStartScreen()
                    }
                    else -> throw IOException()
                }
            })

            viewModel.showBadConnectionDialogAlertFragmentState.observe(
                viewLifecycleOwner,
                Observer { show ->
                    if (show) {
                        viewModel.cancelExercisesJob()
                        activity?.supportFragmentManager?.let {
                            viewModel.snackbarState.removeObservers(viewLifecycleOwner)
                            viewModel.progressState.removeObservers(viewLifecycleOwner)
                            val dialogFragment = ExercisesBadConnectionDialogAlertFragment()
                                .apply {
                                    setTargetFragment(this@ExercisesFragment, 5325)
                                }

                            dialogFragment.show(
                                it,
                                null
                            )
                        }
                    }
                })
        }

        if (savedInstanceState == null) {
            (activity as AppCompatActivity).addFragment(
                ExercisesStartScreenFragment(),
                R.id.exerciseLayoutFrameLayout
            )
        } else {
            if (exercisesViewModel?.answered?.value == true) {
                OneShotPreDrawListener.add(exerciseLayoutSnackbar) {
                    setSnackbarTranslationY()
                    showSnackbar(exercisesViewModel?.snackbarState?.value, animate = false)
                }
            }
        }

        exerciseToolbarCloseButton.setOnClickListener {
            (activity as AppCompatActivity).onBackPressed()
        }

        exerciseBottomSnackbarIcon.setOnClickListener { }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        exercisesViewModel?.let {
            it.progressState.observe(
                viewLifecycleOwner,
                getProgressStateObserver(it)
            )
            it.snackbarState.observe(viewLifecycleOwner, getSnackbarStateObserver())
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (exercisesViewModel?.chapterPartNumber == 2) {

            view?.findViewById<ViewGroup>(R.id.chapterPartTwoHelpLayout)?.let {

                val indexOfCurrentExercise =
                    exercisesViewModel?.exercises?.indexOf(exercisesViewModel?.exerciseData)!!
                val indexOfExerciseScreen =
                    exercisesViewModel?.exercises?.indexOf(exercisesViewModel?.exercisesResponseData?.exercises?.first { it is ExerciseData.ExerciseDataScreen.ScreenType2Data })!!
                val helpIsEnabled = indexOfCurrentExercise < indexOfExerciseScreen

                if (helpIsEnabled && exercisesViewModel?.exercisesProgress?.value == ExercisesViewModel.ExercisesState.EXERCISES) {

                    val hiddenPartWidth =
                        (it.measuredWidth - it.chapterPartTwoHelpLayoutButton.measuredWidth).toFloat()

                    if (ev.action == ACTION_DOWN && it.translationX != hiddenPartWidth && !it.isContains(
                            ev.x.toInt(),
                            ev.y.toInt()
                        )
                    ) {
                        it.animate()
                            .translationX(hiddenPartWidth)
                            .setInterpolator(FastOutSlowInInterpolator()).start()
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun showSnackbar(
        data: Pair<ExerciseUiModel, ExerciseResponseData>?,
        animate: Boolean = true
    ) {

        data?.let {
            val exercisesResponseData = it.second
            formSnackBarView(
                exercisesResponseData.toUiModel(exerciseBottomSnackbarLinearLayout.context)
            )
            exerciseLayoutButton.setOnClickListener {}
            exerciseLayoutSnackbar.post {
                setSnackbarTranslationY()

                exerciseLayoutSnackbar.post {
                    if (animate) {
                        exerciseLayoutSnackbar.animate().translationY(0F)
                            .setDuration(
                                resources.getInteger(R.integer.snackbar_anim_duration).toLong()
                            )
                            .setInterpolator(FastOutSlowInInterpolator()).start()
                    } else {
                        exerciseLayoutSnackbar.translationY = 0F
                    }
                    changeButton(true, animate)
                }
            }
        }
    }

    private fun hideSnackBar() {
        exerciseLayoutSnackbar.animate().translationY(snackbarTranslationY)
            .setInterpolator(
                FastOutSlowInInterpolator()
            ).setDuration(
                resources.getInteger(R.integer.snackbar_anim_duration).toLong()
            ).start()
    }

    private fun setSnackbarTranslationY() {
        val buttonParams =
            exerciseLayoutButton.layoutParams as FrameLayout.LayoutParams
        val snackbarLinearLayoutBottomMargin =
            exerciseLayoutButton.height + buttonParams.bottomMargin
        val snackBarParams =
            exerciseBottomSnackbarLinearLayout.layoutParams as RelativeLayout.LayoutParams
        snackBarParams.bottomMargin = snackbarLinearLayoutBottomMargin
        exerciseBottomSnackbarLinearLayout.layoutParams = snackBarParams

        exerciseLayoutSnackbar.post {
            snackbarTranslationY = exerciseLayoutSnackbar.height.toFloat()
            exerciseLayoutSnackbar.translationY = snackbarTranslationY
            exerciseLayoutSnackbar.visibility = View.VISIBLE
        }
    }


    private fun formSnackBarView(responseData: ExerciseResponseUiModel) {
        exerciseLayoutSnackbar.visibility = View.INVISIBLE
        if (responseData.exerciseResult) {
            formTrueSnackbarAnswerView()
        } else {
            formFalseAnswerSnackbarView(responseData)
        }
    }

    private fun formTrueSnackbarAnswerView() {
        exerciseBottomSnackbarTitle.text = resources.getString(R.string.true_answer)
        exerciseLayoutSnackbar.setBackgroundColor(
            ContextCompat.getColor(exerciseBottomSnackbarLinearLayout.context, R.color.green)
        )
        exerciseBottomSnackbarFlexbox.visibility = View.GONE
        exerciseBottomSnackbarSubtitle.visibility = View.GONE
    }

    private fun formFalseAnswerSnackbarView(responseData: ExerciseResponseUiModel) {
        exerciseLayoutSnackbar.setBackgroundColor(
            ContextCompat.getColor(
                exerciseBottomSnackbarLinearLayout.context,
                R.color.red
            )
        )
        formFalseAnswerSnackbarTitle()
        formFalseAnswerSnackbarSubtitle(responseData)
    }

    private fun formSnackbarFlexboxSubtitle(imagesRowUiModel: ExerciseImagesRowUiModel) {
        exerciseBottomSnackbarSubtitle.visibility = View.GONE
        exerciseBottomSnackbarFlexbox.visibility = View.VISIBLE
        for (i in 0 until imagesRowUiModel.count) {
            val shapeView = SquareView(exerciseBottomSnackbarFlexbox.context)
            val params = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            )
            params.height = 24.dpToPx()
            params.width = 24.dpToPx()
            shapeView.layoutParams = params
            shapeView.background = imagesRowUiModel.image
            exerciseBottomSnackbarFlexbox.addView(shapeView)
        }
    }

    private fun formFalseAnswerSnackbarTitle() {
        if (exercisesViewModel?.exerciseUiModel is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType9UiModel)
            exerciseBottomSnackbarTitle.text =
                resources.getString(R.string.exercise_type_9_false_snackbar_title)
        else
            exerciseBottomSnackbarTitle.text = resources.getString(R.string.false_answer)
    }

    private fun formFalseAnswerSnackbarSubtitle(responseData: ExerciseResponseUiModel) {
        responseData.description?.let { description ->
            if (description.descriptionContent is ExerciseResponseDescriptionContentUiModel.DescriptionContentString) {
                exerciseBottomSnackbarSubtitle.visibility = View.VISIBLE
                exerciseBottomSnackbarFlexbox.visibility = View.GONE
                val descriptionSubtitle = description.descriptionContent.string
                exerciseBottomSnackbarSubtitle.text = descriptionSubtitle
            } else if (description.descriptionContent is ExerciseResponseDescriptionContentUiModel.DescriptionContentArray) {
                val count = description.descriptionContent.image.count
                if (count == 0) {
                    formSnackbarSubtitleForNullShapes()
                } else {
                    exerciseBottomSnackbarFlexbox.removeAllViews()
                    formSnackbarFlexboxSubtitle(description.descriptionContent.image)
                }
            }
        }
    }

    private fun formSnackbarSubtitleForNullShapes() {
        exerciseBottomSnackbarSubtitle.visibility = View.VISIBLE
        exerciseBottomSnackbarFlexbox.visibility = View.GONE
        exerciseBottomSnackbarSubtitle.text = resources.getString(R.string.exercise_image_0_count)
    }

    private fun changeButton(onSnackbarShow: Boolean, animate: Boolean) {
        val context = exerciseLayoutButton.context
        val drawable = exerciseLayoutButton.background as TransitionDrawable
        if (onSnackbarShow) {
            exerciseLayoutButton.setTextColor(
                ThemeUtils.getAccentColor(context)
            )
            drawable.startTransition(if (animate) resources.getInteger(R.integer.button_transition_duration) else 0)
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
            exerciseLayoutButton.setOnClickListener {}
            drawable.reverseTransition(resources.getInteger(R.integer.button_transition_duration))
            exerciseLayoutButton.text = resources.getString(R.string.check)
        }
    }

    private fun formDescriptionView() {
        exerciseLayoutButton.text = resources.getString(R.string.continue_string)
        exerciseLayoutButton.setOnClickListener {
            onExerciseButtonStartExercisesClick()
        }
    }

    private fun formExercisesView() {

        exerciseLayoutButton.setOnClickListener {
            exercisesViewModel?.checkExerciseResult()
        }

        if (exercisesViewModel?.chapterPartNumber == 2) {

            val indexOfCurrentExercise =
                exercisesViewModel?.exercises?.indexOf(exercisesViewModel?.exerciseData)!!
            val indexOfExerciseScreen =
                exercisesViewModel?.exercises?.indexOf(exercisesViewModel?.exercises?.first { it is ExerciseData.ExerciseDataScreen.ScreenType2Data })!!
            val helpIsEnabled = indexOfCurrentExercise < indexOfExerciseScreen

            val helpView =
                exerciseLayoutRelativeLayout.makeView(R.layout.chapter_part_two_help_layout)
                    .apply {

                        exerciseLayoutRelativeLayout.setOnTouchListener(object :
                            OnSwipeTouchListener(context!!) {
                            override fun onSwipeLeft() {
                                if (this@apply.chapterPartTwoHelpLayoutButton.isEnabled) {
                                    if (translationX != 0F) {
                                        animate().translationX(0F)
                                            .setInterpolator(FastOutSlowInInterpolator())
                                            .start()
                                    }
                                }
                            }
                        })
                        chapterPartTwoHelpLayoutButton.setOnSingleClickListener {
                            if (helpIsEnabled) {
                                animate().translationX(if (translationX != 0F) 0F else (measuredWidth - it.measuredWidth).toFloat())
                                    .setInterpolator(FastOutSlowInInterpolator()).start()
                            }
                        }

                        alpha = 0F
                        id = R.id.chapterPartTwoHelpLayout
                        layoutParams = RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            topMargin = 16.dpToPx()
                            addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                            addRule(RelativeLayout.BELOW, R.id.exerciseLayoutToolbar)
                        }
                    }

            exerciseLayoutRelativeLayout.addView(helpView)

            helpView.animate().alpha(1F).start()
            refreshExercisesView()
        }
    }

    private fun formBonusStartScreen() {
        exerciseLayoutButton.setOnClickListener {
            exercisesViewModel?.startExercisesBonus()
        }

    }

    private fun refreshExercisesView() {
        if (exercisesViewModel?.chapterPartNumber == 2) {

            val indexOfCurrentExercise =
                exercisesViewModel?.exercises?.indexOf(exercisesViewModel?.exerciseData)!!
            val indexOfExerciseScreen =
                exercisesViewModel?.exercises?.indexOf(exercisesViewModel?.exercises?.first { it is ExerciseData.ExerciseDataScreen.ScreenType2Data })!!
            val helpIsEnabled = indexOfCurrentExercise < indexOfExerciseScreen

            if (helpIsEnabled && exercisesViewModel?.exercisesProgress?.value == ExercisesViewModel.ExercisesState.EXERCISES) {

                view?.findViewById<ViewGroup>(R.id.chapterPartTwoHelpLayout)?.let {

                    it.measure(0, 0)

                    val prevMeasuredWidth = it.measuredWidth

                    when (val exerciseUiModel = exercisesViewModel?.exerciseUiModel) {
                        is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType3UiModel -> {
                            it.chapterPartTwoHelpLayoutNumberText.text =
                                exerciseUiModel.title.symbol
                            it.chapterPartTwoHelpLayoutNumberNameText.text =
                                exerciseUiModel.title.symbolName
                        }
                        is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType4UiModel -> {
                            it.chapterPartTwoHelpLayoutNumberText.text =
                                exerciseUiModel.title.symbol
                            it.chapterPartTwoHelpLayoutNumberNameText.text =
                                exerciseUiModel.title.symbolName
                        }
                    }

                    it.measure(0, 0)

                    val translationX = (it.measuredWidth-it.chapterPartTwoHelpLayoutButton.measuredWidth).toFloat()

                    if(it.translationX == prevMeasuredWidth.toFloat()) {
                        it.translationX = it.measuredWidth.toFloat()
                        it.animate()
                            .translationX(translationX)
                            .setInterpolator(FastOutSlowInInterpolator()).start()
                    }else{
                        it.translationX = translationX
                    }
                }
            } else {
                val helpView = view?.findViewById<ViewGroup>(R.id.chapterPartTwoHelpLayout)
                helpView?.let {
                    it.measure(0, 0)
                    it.chapterPartTwoHelpLayoutButton.isEnabled = false

                    if(it.translationX != 0F)
                        it.animate().translationX(helpView.measuredWidth.toFloat()).setInterpolator(FastOutSlowInInterpolator())?.start()
                    else
                        it.translationX = helpView.measuredWidth.toFloat()
                }
            }
        }
    }

    private fun setButtonText() {
        exercisesViewModel?.apply {
            val exerciseData = this.exerciseData
            setButtonText(
                when {
                    exerciseData is ExerciseData.ExerciseDataScreen.ScreenType4Data && exerciseData.isBonusStart -> {
                        resources.getString(R.string.begin)
                    }
                    exerciseData is ExerciseData.ExerciseDataExercise -> resources.getString(R.string.check)
                    exerciseData is ExerciseData.ExerciseDataScreen -> {
                        if (exerciseData.exerciseNumber == null)
                            resources.getString(R.string.continue_string)
                        else
                            resources.getString(R.string.next)
                    }
                    else -> throw IOException()
                }
            )
        }
    }

    private fun onExerciseButtonStartExercisesClick() {
        exercisesViewModel?.let {
            it.initFragment()
            it.animateProgressBarVisibility(true)
            it.setExercisesProgress(ExercisesViewModel.ExercisesState.EXERCISES)

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                delay(resources.getInteger(R.integer.navigatable_fragment_anim_duration).toLong())
                it.showBars(false)
            }
        }
    }

    private fun getProgressStateObserver(exercisesViewModel: ExercisesViewModel) =
        Observer<Pair<Float, Boolean>> { pair ->
            val progress = pair.first
            val last = pair.second
            exerciseToolbarProgressBar.animateProgress(
                toPercent = progress,
                delay = (if ((exerciseToolbarProgressBar.animation == null) || this.exerciseToolbarProgressBar.animation.hasEnded()) 100L else 0L),
                onAnimEnd = {
                    if (last) {
                        exerciseLayoutButton.setOnClickListener {}
                        exercisesViewModel.saveUserDataState.observe(
                            viewLifecycleOwner,
                            Observer { pair ->
                                val saved = pair.first
                                if (saved) {
                                    (activity as AppCompatActivity).navigateTo(
                                        exercisesViewModel.getExercisesEndFragment(
                                            pair.second
                                        ), R.id.frameLayout
                                    )
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