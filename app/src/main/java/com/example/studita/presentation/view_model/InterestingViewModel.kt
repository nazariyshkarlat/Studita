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
import com.example.studita.presentation.model.mapper.InterestingUiModelMapper
import com.example.studita.utils.PrefsUtils
import com.example.studita.utils.launchExt
import kotlinx.coroutines.Job

class InterestingViewModel : ViewModel(){

    val interestingState = SingleLiveEvent<Boolean>()
    val errorState = SingleLiveEvent<Int>()
    val progressBarState = SingleLiveEvent<Pair<Float, Boolean>>()
    var interestingProgress =  MutableLiveData<InterestingState>(InterestingState.START_SCREEN)
    var toolbarDividerState = SingleLiveEvent<Boolean>()
    var buttonDividerState = SingleLiveEvent<Boolean>()
    val navigationState = SingleLiveEvent<Pair<InterestingNavigationState, Fragment>>()

    var interestingNumber: Int = 0
    private var currentScreenPosition = 0
    private lateinit var interestingScreens: List<InterestingUiModelScreen>

    lateinit var currentScreen: InterestingUiModelScreen

    private val interestingInteractor = InterestingModule.getInterestingInteractorImpl()

    private var job: Job? = null

    fun getInteresting(interestingNumber: Int){
        job = viewModelScope.launchExt(job){
            when(val status = interestingInteractor.getInteresting(interestingNumber, PrefsUtils.isOfflineMode())){
                is InterestingStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                is InterestingStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                is InterestingStatus.Success -> {
                    interestingState.postValue(true)
                    interestingScreens = InterestingUiModelMapper().map(status.result).screens
                    this@InterestingViewModel.interestingNumber = status.result.interestingNumber
                }
            }
        }
    }

    fun initFragment(){
        currentScreen = interestingScreens[currentScreenPosition]
        navigationState.value = getFragmentToAdd(currentScreen)
        setProgressPercent(getProgressPercent())
        currentScreenPosition++
    }

    private fun getFragmentToAdd(interestingUiModelScreen: InterestingUiModelScreen) =
        when(interestingUiModelScreen){
            is InterestingUiModelScreen.InterestingUiModelStartScreen -> {
                setInterestingProgress(InterestingState.START_SCREEN)
                InterestingNavigationState.ADD to InterestingStartScreenFragment()
            }
            is InterestingUiModelScreen.InterestingUiModelStepScreen -> {
                setInterestingProgress(InterestingState.STEP)
                (if (currentScreenPosition == 1) InterestingNavigationState.NAVIGATE else InterestingNavigationState.REPLACE) to InterestingStepFragment()
            }
            is InterestingUiModelScreen.InterestingUiModelSpecificDrumRollScreen -> {
                setInterestingProgress(InterestingState.STEP)
                InterestingNavigationState.REPLACE to InterestingSpecificDrumRollFragment()
            }
            is InterestingUiModelScreen.InterestingUiModelExplanationScreen -> {
                setInterestingProgress(InterestingState.EXPLANATION)
                InterestingNavigationState.NAVIGATE to InterestingExplanation1Fragment()
            }
        }


    fun showToolbarDivider(show: Boolean){
        toolbarDividerState.value = show
    }

    fun showButtonDivider(show: Boolean){
        buttonDividerState.value = show
    }

    private fun setProgressPercent(percent: Float){
        progressBarState.value = percent to (percent == 1F)
    }

    private fun setInterestingProgress(state: InterestingState){
        interestingProgress.value = state
    }

    private fun getProgressPercent(): Float = (currentScreenPosition-1)/(interestingScreens.lastIndex-1).toFloat()

    enum class InterestingNavigationState{
        ADD,
        NAVIGATE,
        REPLACE
    }

    enum class InterestingState{
        START_SCREEN,
        STEP,
        EXPLANATION
    }

}