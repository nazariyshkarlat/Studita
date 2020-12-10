package com.studita.presentation.view_model

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studita.R
import com.studita.di.data.InterestingModule
import com.studita.domain.entity.InterestingLikeData
import com.studita.domain.entity.InterestingLikeRequestData
import com.studita.domain.entity.exercise.ExerciseRequestData
import com.studita.domain.interactor.InterestingStatus
import com.studita.presentation.fragments.interesting.*
import com.studita.presentation.model.InterestingUiModelScreen
import com.studita.presentation.model.toInterestingUiModel
import com.studita.utils.PrefsUtils
import com.studita.utils.UserUtils
import com.studita.utils.launchExt
import kotlinx.coroutines.*

class InterestingViewModel(val interestingNumber: Int) : ViewModel() {

    val interestingState = MutableLiveData<Boolean>()
    val progressBarState = SingleLiveEvent<Pair<Float, Boolean>>()
    var interestingProgress = MutableLiveData<InterestingState>(InterestingState.START_SCREEN)
    var feedbackState = MutableLiveData<FeedbackState>(FeedbackState.NO_FEEDBACK)
    var feedbackEvent = MutableLiveData<Boolean>()
    var toolbarDividerState = SingleLiveEvent<Boolean>()
    var buttonDividerState = SingleLiveEvent<Boolean>()
    val navigationState = SingleLiveEvent<Pair<InterestingNavigationState, Fragment>>()
    val loadScreenBadConnectionState = MutableLiveData<Boolean>()

    private val connectionTimeout = 3000L

    private var currentScreenPosition = -1
    var interestingScreens: List<InterestingUiModelScreen>? = null

    lateinit var currentScreen: InterestingUiModelScreen

    private val interestingInteractor = InterestingModule.getInterestingInteractorImpl()

    private var job: Job? = null
    var badConnectionJob: Job? = null

    init {
        getInteresting()
    }


    fun getInteresting() {

        runBadConnectionCoroutine()

        job = viewModelScope.launchExt(job) {
            when (val status = interestingInteractor.getInteresting(
                interestingNumber,
                PrefsUtils.isOfflineModeEnabled()
            )) {
                is InterestingStatus.NoConnection -> loadScreenBadConnectionState.value = true
                is InterestingStatus.ServiceUnavailable -> loadScreenBadConnectionState.value = true
                is InterestingStatus.Success -> {
                    interestingState.value = true
                    interestingScreens = status.result.toInterestingUiModel().screens
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

    private fun runBadConnectionCoroutine() {

        badConnectionJob = viewModelScope.launch(Dispatchers.Main) {
            delay(connectionTimeout)

            if(loadScreenBadConnectionState.value == null)
                loadScreenBadConnectionState.value = true
        }

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
        feedbackEvent(feedbackState == FeedbackState.LIKE)
        this.feedbackState.value = feedbackState
    }

    private fun feedbackEvent(likeIt: Boolean){
        if(feedbackEvent.value == null){
             GlobalScope.launch(Dispatchers.Main   ) {
                feedbackEvent.value = likeIt
                interestingInteractor.sendInterestingLike(InterestingLikeRequestData(UserUtils.getUserIDTokenData(), InterestingLikeData(interestingNumber, likeIt)))
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