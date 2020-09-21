package com.example.studita.presentation.view_model

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.di.data.InterestingModule
import com.example.studita.domain.interactor.InterestingStatus
import com.example.studita.presentation.fragments.interesting.*
import com.example.studita.presentation.model.InterestingUiModelScreen
import com.example.studita.presentation.model.toInterestingUiModel
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.launchExt
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class InterestingViewModel : ViewModel() {

    val interestingState = SingleLiveEvent<Boolean>()
    val errorState = SingleLiveEvent<Int>()
    val progressBarState = SingleLiveEvent<Pair<Float, Boolean>>()
    var interestingProgress = MutableLiveData<InterestingState>(InterestingState.START_SCREEN)
    var feedbackState = MutableLiveData<FeedbackState>(FeedbackState.NO_FEEDBACK)
    var feedbackEvent = MutableLiveData<Unit>()
    var toolbarDividerState = SingleLiveEvent<Boolean>()
    var buttonDividerState = SingleLiveEvent<Boolean>()
    val navigationState = SingleLiveEvent<Pair<InterestingNavigationState, Fragment>>()

    var interestingNumber: Int = 0
    private var currentScreenPosition = -1
    var interestingScreens: List<InterestingUiModelScreen>? = null

    lateinit var currentScreen: InterestingUiModelScreen

    private val interestingInteractor = InterestingModule.getInterestingInteractorImpl()

    private var job: Job? = null

    fun getInteresting(interestingNumber: Int) {
        job = viewModelScope.launchExt(job) {
            when (val status = interestingInteractor.getInteresting(
                interestingNumber,
                PrefsUtils.isOfflineModeEnabled()
            )) {
                is InterestingStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                is InterestingStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                is InterestingStatus.Success -> {
                    interestingState.postValue(true)
                    interestingScreens = status.result.toInterestingUiModel().screens
                    this@InterestingViewModel.interestingNumber = status.result.interestingNumber
                }
            }
        }
    }

    fun initFragment() {
        currentScreenPosition++
        currentScreen = interestingScreens!![currentScreenPosition]
        navigationState.value = getFragmentToAdd(currentScreen)
        executeInterestingProgress(currentScreen)
        setProgressPercent(getProgressPercent())
    }

    fun onBackClick(){
        currentScreenPosition--
        currentScreen = interestingScreens!![currentScreenPosition]
        executeInterestingProgress(currentScreen)
        setProgressPercent(getProgressPercent())
    }

    private fun getFragmentToAdd(interestingUiModelScreen: InterestingUiModelScreen) =
        when (interestingUiModelScreen) {
            is InterestingUiModelScreen.InterestingUiModelStartScreen -> {
                InterestingNavigationState.ADD to InterestingStartScreenFragment()
            }
            is InterestingUiModelScreen.InterestingUiModelStepScreen -> {
                (if (currentScreenPosition == 1) InterestingNavigationState.NAVIGATE else InterestingNavigationState.REPLACE) to InterestingStepFragment()
            }
            is InterestingUiModelScreen.InterestingUiModelSpecificDrumRollScreen -> {
                InterestingNavigationState.REPLACE to InterestingSpecificDrumRollFragment()
            }
            is InterestingUiModelScreen.InterestingUiModelExplanationScreen -> {
                InterestingNavigationState.NAVIGATE to InterestingExplanation1Fragment()
            }
        }

    private fun executeInterestingProgress(interestingUiModelScreen: InterestingUiModelScreen){
        when(interestingUiModelScreen){
            is InterestingUiModelScreen.InterestingUiModelStartScreen -> {
                setInterestingProgress(InterestingState.START_SCREEN)
            }
            is InterestingUiModelScreen.InterestingUiModelStepScreen -> {
                setInterestingProgress(InterestingState.STEP)
            }
            is InterestingUiModelScreen.InterestingUiModelSpecificDrumRollScreen -> {
                setInterestingProgress(InterestingState.STEP)
            }
            is InterestingUiModelScreen.InterestingUiModelExplanationScreen -> {
                setInterestingProgress(InterestingState.EXPLANATION)
            }
        }
    }


    fun showToolbarDivider(show: Boolean) {
        toolbarDividerState.value = show
    }

    fun showButtonDivider(show: Boolean) {
        buttonDividerState.value = show
    }

    private fun setProgressPercent(percent: Float) {
        progressBarState.value = percent to (percent == 1F)
    }

    private fun setInterestingProgress(state: InterestingState) {
        interestingProgress.value = state
    }

    private fun getProgressPercent(): Float =
        interestingScreens?.let{ (currentScreenPosition - 1) / (it.lastIndex - 1).toFloat()} ?: 0F

    fun setFeedback(feedbackState: FeedbackState){
        feedbackEvent()
        this.feedbackState.value = feedbackState
    }

    private fun feedbackEvent(){
        if(feedbackEvent.value == null){
            viewModelScope.launch {
                delay(500)
                feedbackEvent.value = Unit
            }
        }
    }

    enum class InterestingNavigationState {
        ADD,
        NAVIGATE,
        REPLACE
    }

    enum class InterestingState {
        START_SCREEN,
        STEP,
        EXPLANATION
    }

    enum class FeedbackState {
        NO_FEEDBACK,
        LIKE,
        DISLIKE
    }

}