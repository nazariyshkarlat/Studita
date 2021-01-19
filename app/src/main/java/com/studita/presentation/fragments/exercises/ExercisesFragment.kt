package com.studita.presentation.fragments.exercises

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.DialogInterface
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.OneShotPreDrawListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import com.studita.R
import com.studita.domain.entity.exercise.ExerciseData
import com.studita.domain.entity.exercise.ExerciseResponseData
import com.studita.presentation.fragments.base.BaseFragment
import com.studita.presentation.fragments.bottom_sheets.ExerciseReportBugBottomSheetFragment
import com.studita.presentation.fragments.dialog_alerts.ExercisesBadConnectionDialogAlertFragment
import com.studita.presentation.fragments.exercises.description.ExercisesDescriptionFragment
import com.studita.presentation.listeners.OnSingleClickListener.Companion.setOnSingleClickListener
import com.studita.presentation.model.*
import com.studita.presentation.view_model.ExercisesViewModel
import com.studita.presentation.views.SquareView
import com.studita.utils.*
import com.google.android.flexbox.FlexboxLayout
import kotlinx.android.synthetic.main.exercise_bottom_snackbar.*
import kotlinx.android.synthetic.main.exercise_layout.*
import kotlinx.android.synthetic.main.exercise_layout.exerciseLayoutParentView
import kotlinx.android.synthetic.main.exercise_toolbar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException


class ExercisesFragment : BaseFragment(R.layout.exercise_layout), DialogInterface.OnDismissListener{

    var exercisesViewModel: ExercisesViewModel? = null
    private var snackbarTranslationY = 0F

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exercisesViewModel = activity?.run {
            ViewModelProviders.of(this).get(ExercisesViewModel::class.java)
        }

        exercisesViewModel?.let { viewModel ->

            viewModel.navigationEvent.observe(viewLifecycleOwner, Observer { pair ->

                when (pair.first) {
                    ExercisesViewModel.ExercisesNavigationState.FIRST -> {

                        replace(
                            pair.second,
                            R.id.exerciseLayoutFrameLayout,
                            addToBackStack = false
                        )
                    }
                    ExercisesViewModel.ExercisesNavigationState.REPLACE -> {

                        Handler().postDelayed({
                            refreshExercisesView()
                        }, 300)

                        replace(
                            pair.second,
                            R.id.exerciseLayoutFrameLayout,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right,
                            addToBackStack = false
                        )
                    }
                    ExercisesViewModel.ExercisesNavigationState.NAVIGATE_IN_CONTAINER -> {
                        replace(
                            pair.second,
                            R.id.exerciseLayoutFrameLayout,
                            addToBackStack = false
                        )
                    }
                    ExercisesViewModel.ExercisesNavigationState.NAVIGATE_COMPLETELY -> {
                        (activity as AppCompatActivity).replace(
                            pair.second,
                            R.id.frameLayout,
                            addToBackStack = false
                        )
                    }
                }
                setButtonText()
            })

            viewModel.snackbarEvent.observe(viewLifecycleOwner, getSnackbarStateObserver())

            viewModel.selectVariantEvent.observe(viewLifecycleOwner, Observer {
                with(getButtonTextDuringExercise(it, exercisesViewModel!!.exerciseCountToSelect, exercisesViewModel!!.isInputExercise())){
                    exerciseLayoutButton.text = this
                    exercisesViewModel?.setButtonText(this)
                }
            })

            viewModel.buttonEnabledState.observe(viewLifecycleOwner, Observer { enabled ->
                exerciseLayoutButton.isEnabled = enabled

                if(enabled){
                    exerciseLayoutButton.text = resources.getString(R.string.check)
                    exercisesViewModel?.setButtonText(resources.getString(R.string.check))
                }else if(exercisesViewModel!!.isInputExercise() || exercisesViewModel!!.exerciseCountToSelect <= 1){
                    with(getButtonTextDuringExercise(0, exercisesViewModel!!.exerciseCountToSelect, exercisesViewModel!!.isInputExercise())){
                        exerciseLayoutButton.text = this
                        exercisesViewModel?.setButtonText(this)
                    }
                }

                exerciseLayoutButton.setOnClickListener {
                    exercisesViewModel?.checkExerciseResult()
                }
            })

            viewModel.getToolbarProgressBarAnimEvent()?.let {
                viewModel.setProgressBarVisibility(it)
            }

            viewModel.buttonTextState.observe(viewLifecycleOwner, Observer { text ->
                exerciseLayoutButton.text = text
            })

            viewModel.toolbarDividerEvent.observe(viewLifecycleOwner, Observer { show ->
                exerciseLayoutToolbar.background = if (show) ContextCompat.getDrawable(
                    context!!,
                    R.drawable.divider_bottom_drawable
                ) else null
            })

            viewModel.buttonDividerEvent.observe(viewLifecycleOwner, Observer { show ->
                exerciseLayoutButtonFrameLayout.background = if (show) ContextCompat.getDrawable(
                    context!!,
                    R.drawable.divider_top_drawable,
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

            viewModel.progressEvent.observe(viewLifecycleOwner, getProgressStateObserver(viewModel))

            viewModel.exercisesProgress.observe(viewLifecycleOwner, Observer { state ->
                when (state) {
                    ExercisesViewModel.ExercisesState.START_SCREEN -> {
                        exerciseLayoutButton.setOnClickListener {
                            exercisesViewModel?.startSecondsCounter()
                            exercisesViewModel?.timeCounterIsPaused = false
                            if (viewModel.exercisesResponseData!!.exercisesDescription != null) {
                                viewModel.setExercisesProgress(ExercisesViewModel.ExercisesState.DESCRIPTION)
                            } else {
                                onExerciseButtonStartExercisesClick()
                            }
                        }
                    }
                    ExercisesViewModel.ExercisesState.DESCRIPTION -> {
                        if (childFragmentManager.findFragmentById(R.id.exerciseLayoutFrameLayout) !is ExercisesDescriptionFragment)
                            replace(
                                viewModel.getDescriptionFragment(),
                                R.id.exerciseLayoutFrameLayout,
                                addToBackStack = false
                            )
                        formDescriptionView()
                    }
                    ExercisesViewModel.ExercisesState.EXERCISES -> {
                        formExercisesView()
                    }
                    ExercisesViewModel.ExercisesState.BONUS_SCREEN -> {
                        formBonusStartScreen()
                    }
                    ExercisesViewModel.ExercisesState.EXPLANATION -> {
                        formExplanationView()
                    }
                    else -> throw IOException()
                }
            })

            viewModel.showBadConnectionDialogAlertFragmentEvent.observe(
                viewLifecycleOwner,
                Observer { show ->
                    if (show && activity?.supportFragmentManager?.findFragmentByTag(ExercisesBadConnectionDialogAlertFragment.BAG_CONNECTION_DIALOG) == null) {
                        activity?.supportFragmentManager?.let {
                            viewModel.snackbarEvent.removeObservers(viewLifecycleOwner)
                            viewModel.progressEvent.removeObservers(viewLifecycleOwner)
                            val dialogFragment = ExercisesBadConnectionDialogAlertFragment()
                                .apply {
                                    setTargetFragment(this@ExercisesFragment, 5325)
                                }

                            dialogFragment.show(
                                it,
                                ExercisesBadConnectionDialogAlertFragment.BAG_CONNECTION_DIALOG
                            )
                        }
                    }
                })
        }

        if (savedInstanceState == null) {
            addFragment(
                ExercisesStartScreenFragment(),
                R.id.exerciseLayoutFrameLayout
            )
        } else {
            if (exercisesViewModel?.answered?.value == true) {
                OneShotPreDrawListener.add(exerciseLayoutSnackbar) {
                    setSnackbarTranslationY()
                    showSnackbar(exercisesViewModel?.getSnackbarState(), animate = false)
                }
            }
        }

        exerciseToolbarLeftIcon.setImageResource(R.drawable.ic_close)
        exerciseToolbarLeftIcon.setOnClickListener {
            (activity as AppCompatActivity).onBackPressed()
        }

        exerciseBottomSnackbarIcon.setOnSingleClickListener {
            activity?.supportFragmentManager?.let{ExerciseReportBugBottomSheetFragment().apply {
                arguments = bundleOf("EXERCISE_NUMBER" to exercisesViewModel?.exerciseData?.exerciseNumber)
            }.show(it, null)}
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        exercisesViewModel?.let {
            if (it.chapterPartIsFullyCompleted()) {
                it.saveObtainedExercisesResult()
            } else {
                it.snackbarEvent.observe(viewLifecycleOwner, getSnackbarStateObserver())
            }
            it.progressEvent.observe(
                viewLifecycleOwner,
                getProgressStateObserver(it)
            )
        }
    }

    private fun showSnackbar(
        data: Pair<ExerciseUiModel, ExerciseResponseData>?,
        animate: Boolean = true
    ) {

        exerciseBottomSnackbarIcon.isEnabled = false
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
                            ).setListener(object : AnimatorListenerAdapter(){
                                override fun onAnimationEnd(animation: Animator?) {
                                    super.onAnimationEnd(animation)
                                    if(exerciseBottomSnackbarIcon != null)
                                        exerciseBottomSnackbarIcon.isEnabled = true
                                }
                            })
                            .setInterpolator(FastOutSlowInInterpolator()).start()
                    } else {
                        exerciseBottomSnackbarIcon.isEnabled = true
                        exerciseLayoutSnackbar.translationY = 0F
                    }
                    changeButton(true, animate)
                }
            }
        }
    }

    private fun hideSnackBar() {
        exerciseBottomSnackbarIcon.isEnabled = false
        exerciseLayoutSnackbar.animate().setListener(null).translationY(snackbarTranslationY)
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
        exerciseLayoutSnackbar.setBackgroundResource(
            R.drawable.exercise_snackbar_correct_answer
        )
        exerciseBottomSnackbarFlexbox.visibility = View.GONE
        exerciseBottomSnackbarSubtitle.visibility = View.GONE
    }

    private fun formFalseAnswerSnackbarView(responseData: ExerciseResponseUiModel) {
        exerciseLayoutSnackbar.setBackgroundResource(
            R.drawable.exercise_snackbar_incorrect_answer
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
            params.height = 24F.dp
            params.width = 24F.dp
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
                if(exercisesViewModel?.exercisesAreCompletedAndNoBonus == false) {
                    hideSnackBar()
                    changeButton(false, animate)
                }
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

            exerciseLayoutParentView.enableHelpView()
            refreshExercisesView()

        }
    }

    private fun refreshExercisesView(){

        if (exercisesViewModel?.chapterPartNumber == 2) {
            val indexOfCurrentExercise =
                exercisesViewModel?.exercises?.indexOf(exercisesViewModel?.exerciseData)!!
            val indexOfExerciseScreen =
                exercisesViewModel?.exercises?.indexOf(exercisesViewModel?.exercises?.first { it is ExerciseData.ExerciseDataScreen.ScreenType2Data })!!
            val helpIsEnabled = indexOfCurrentExercise < indexOfExerciseScreen

            if (helpIsEnabled && exercisesViewModel?.exercisesProgress?.value == ExercisesViewModel.ExercisesState.EXERCISES) {

                exerciseLayoutParentView.enableHelpView()

                when (val exerciseUiModel = exercisesViewModel?.exerciseUiModel) {
                    is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType3UiModel -> {
                        exerciseLayoutParentView.setText(
                            exerciseUiModel.title.symbol,
                            exerciseUiModel.title.symbolName
                        )
                    }
                    is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType4UiModel -> {
                        exerciseLayoutParentView.setText(
                            exerciseUiModel.title.symbol,
                            exerciseUiModel.title.symbolName
                        )
                    }
                }
            } else {
                if(exerciseLayoutParentView != null) {
                    exerciseLayoutParentView.disableHelpView()
                    exerciseLayoutParentView.hideView()
                }
            }
        }
    }

    private fun formBonusStartScreen() {
        exerciseLayoutButton.setOnClickListener {
            exercisesViewModel?.startExercisesBonus()
        }
    }

    private fun formExplanationView(){
        exercisesViewModel?.let {vm->
            exerciseLayoutButton.setOnClickListener {
                vm.checkExerciseResult()
                vm.setExercisesProgress(ExercisesViewModel.ExercisesState.EXERCISES)
                if(exercisesViewModel?.exercisesAreCompleted == false) {
                    vm.viewModelScope.launch(Dispatchers.Main) {
                        delay(
                            resources.getInteger(R.integer.exercises_slide_anim_duration).toLong()
                        )
                        vm.showTransparentLayouts(false)
                    }
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
                    exerciseData is ExerciseData.ExerciseDataExercise -> {
                        getButtonTextDuringExercise(0, exercisesViewModel!!.exerciseCountToSelect,
                            exercisesViewModel!!.isInputExercise()
                        )
                    }
                    exerciseData is ExerciseData.ExerciseDataScreen -> {
                        if (exerciseData.exerciseNumber == null)
                            resources.getString(R.string.continue_string)
                        else
                            resources.getString(R.string.next)
                    }
                    exerciseData is ExerciseData.ExerciseExplanationData -> {
                        resources.getString(R.string.continue_string)
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

            it.viewModelScope.launch(Dispatchers.Main) {
                delay(resources.getInteger(R.integer.navigatable_fragment_anim_duration).toLong())
                it.showTransparentLayouts(false)
            }
        }
    }

    private fun getProgressStateObserver(exercisesViewModel: ExercisesViewModel) =
        Observer<Pair<Float, Boolean>> { pair ->
            val progress = pair.first
            val last = pair.second

            if (last) {
                exerciseLayoutButton.setOnClickListener {}
                exercisesViewModel.saveUserDataEvent.observe(
                    viewLifecycleOwner,
                    Observer {
                        val saved = it.first
                        if (saved) {
                            (activity as AppCompatActivity).replace(
                                exercisesViewModel.getExercisesEndFragment(
                                    it.second
                                ), R.id.frameLayout,
                                addToBackStack = false
                            )
                        }
                    })
            }else {
                exerciseToolbarProgressBar.animateProgress(
                    toPercent = progress,
                    delay = (if ((exerciseToolbarProgressBar.animation == null) || this.exerciseToolbarProgressBar.animation.hasEnded()) 100L else 0L)
                )
            }
        }

    private fun getButtonTextDuringExercise(selectedCount: Int, countToSelect: Int, isInputExercise: Boolean): String{
        return when {
            exercisesViewModel!!.buttonEnabledState.value == true-> {
                resources.getString(R.string.check)
            }
            isInputExercise -> {
                resources.getString(R.string.input_answer)
            }
            else -> {
                LanguageUtils.getResourcesRussianLocale(activity!!).getQuantityString(
                    if(selectedCount == 0) R.plurals.select_answer_plurals else R.plurals.select_more_answers_plurals,
                    countToSelect-selectedCount,
                    countToSelect-selectedCount
                )
            }
        }
    }

    private fun getSnackbarStateObserver() =
        Observer<Pair<ExerciseUiModel, ExerciseResponseData>?> { response ->
            showSnackbar(response)
        }

}