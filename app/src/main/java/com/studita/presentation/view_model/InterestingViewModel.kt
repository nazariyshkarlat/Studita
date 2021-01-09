package com.studita.presentation.view_model

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studita.domain.entity.InterestingLikeData
import com.studita.domain.entity.InterestingLikeRequestData
import com.studita.domain.interactor.InterestingStatus
import com.studita.domain.interactor.interesting.InterestingInteractor
import com.studita.presentation.fragments.interesting.*
import com.studita.presentation.model.InterestingUiModelScreen
import com.studita.presentation.model.toInterestingUiModel
import com.studita.utils.*
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.context.GlobalContext

class InterestingViewModel(private val interestingNumber: Int, private val handle: SavedStateHandle) : ViewModel() {

    companion object{
        const val FEEDBACK_STATE = "FEEDBACK_STATE"
        const val PROGRESS_BAR_STATE = "PROGRESS_BAR_STATE"
        const val INTERESTING_PROGRESS = "INTERESTING_PROGRESS"
        const val CURRENT_SCREEN = "CURRENT_SCREEN"
        const val INTERESTING_SCREENS = "INTERESTING_SCREENS"
        const val CURRENT_SCREEN_POSITION = "CURRENT_SCREEN_POSITION"
    }

    val interestingDataReceivedEvent = SingleLiveEvent<Boolean>()
    val progressBarAnimationEvent = SingleLiveEvent<Pair<Float, Boolean>>()
    var interestingProgress = MutableLiveData<InterestingState>(InterestingState.START_SCREEN)
    var feedbackState = MutableLiveData<FeedbackState>(FeedbackState.NO_FEEDBACK)
    var feedbackEvent = MutableLiveData<Boolean>()
    var showToolbarDividerEvent = SingleLiveEvent<Boolean>()
    var showButtonDividerEvent = SingleLiveEvent<Boolean>()
    val navigationEvent = SingleLiveEvent<Pair<InterestingNavigationState, Fragment>>()
    val loadScreenBadConnectionState = MutableLiveData<Boolean>()

    private val connectionTimeout = 3000L

    private var currentScreenPosition: Int = -1
    var interestingScreens: List<InterestingUiModelScreen>? = null

    var currentScreen: InterestingUiModelScreen? = null

    private val interestingInteractor = GlobalContext.get().get<InterestingInteractor>()

    private var job: Job? = null
    var badConnectionJob: Job? = null

    init {
        restoreState()

        if(interestingScreens == null)
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
                    interestingDataReceivedEvent.value = true
                    interestingScreens = status.result.toInterestingUiModel().screens
                }
            }
        }
    }

    fun initFragment() {
        currentScreenPosition++
        currentScreen = interestingScreens!![currentScreenPosition]
        navigationEvent.value = getFragmentToAdd(currentScreen!!)
        executeInterestingProgress(currentScreen!!)
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
        executeInterestingProgress(currentScreen!!)
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


    fun getProgressBarState() : Pair<Float, Boolean>{
        return if(progressBarAnimationEvent.value == null){
            savedStateProgressBarListToPair()
        }else
            progressBarAnimationEvent.value!!
    }

    private fun savedStateProgressBarListToPair() = handle.get<List<Any>>(PROGRESS_BAR_STATE)!![0] as Float to handle.get<List<Any>>(PROGRESS_BAR_STATE)!![1] as Boolean


    fun showToolbarDivider(show: Boolean) {
        showToolbarDividerEvent.value = show
    }

    fun showButtonDivider(show: Boolean) {
        showButtonDividerEvent.value = show
    }

    private fun setProgressPercent(percent: Float) {
        progressBarAnimationEvent.value = percent to (percent == 1F)
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

    fun saveState(){
        interestingScreens?.let {
            handle.set(
                INTERESTING_SCREENS,
                Json.encodeToString(it)
            )
        }
        if(currentScreenPosition != -1)
            handle.set(CURRENT_SCREEN_POSITION, currentScreenPosition)
        currentScreen?.let {
            handle.set(
                CURRENT_SCREEN,
                Json.encodeToString(it)
            )
        }
        progressBarAnimationEvent.value?.let {
            handle.set(
                PROGRESS_BAR_STATE,
                listOf<Any>(
                    it.first,
                    it.second
                )
            )
        }
        interestingProgress.value?.let {
            handle.set(INTERESTING_PROGRESS, it.ordinal)
        }
        feedbackState.value?.let{
            handle.set(FEEDBACK_STATE, it.ordinal)
        }
    }

    private fun restoreState(){
        handle.get<String>(CURRENT_SCREEN)?.let{
            currentScreen = Json.decodeFromString<InterestingUiModelScreen>(it)
        }
        handle.get<String>(INTERESTING_SCREENS)?.let{
            interestingScreens = Json.decodeFromString<List<InterestingUiModelScreen>>(it)
        }
        handle.get<Int>(CURRENT_SCREEN_POSITION)?.let{
            currentScreenPosition = it
        }
        handle.get<Int>(INTERESTING_PROGRESS)?.let{
            interestingProgress.value = InterestingState.values()[it]
        }
        handle.get<Int>(FEEDBACK_STATE)?.let{
            feedbackState.value = FeedbackState.values()[it]
        }
    }

}