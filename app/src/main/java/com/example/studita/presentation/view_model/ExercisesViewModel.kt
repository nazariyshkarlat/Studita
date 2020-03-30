package com.example.studita.presentation.view_model

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.di.exercise.ExerciseResultModule
import com.example.studita.di.exercise.ExercisesModule
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.domain.entity.exercise.ExerciseResponseData
import com.example.studita.domain.entity.exercise.ExercisesResponseData
import com.example.studita.domain.interactor.ExerciseResultStatus
import com.example.studita.domain.interactor.ExercisesStatus
import com.example.studita.presentation.utils.launchExt
import com.example.studita.presentation.fragments.ExercisesEndFragment
import com.example.studita.presentation.fragments.exercise.*
import com.example.studita.presentation.fragments.exercise_screen.ExerciseScreenType1
import com.example.studita.presentation.fragments.exercise_screen.ExerciseScreenType2
import com.example.studita.presentation.fragments.exercise_screen.ExerciseScreenType3
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.model.mapper.ExercisesUiModelMapper
import kotlinx.coroutines.Job
import java.util.*
import kotlin.collections.ArrayList

class ExercisesViewModel : ViewModel(){

    val exercisesState = SingleLiveEvent<Boolean>()
    val exercisesEndTextButtonState = SingleLiveEvent<Boolean>()
    val navigationState = SingleLiveEvent<Pair<ExercisesNavigationState, Fragment>>()
    val progressBarState = SingleLiveEvent<Pair<Int, Boolean>>()
    val answered = MutableLiveData<Boolean>()
    val snackbarState = SingleLiveEvent<Pair<ExerciseUiModel, ExerciseResponseData>?>()
    val errorState = SingleLiveEvent<Int>()
    val exercisesButtonEnabledState = MutableLiveData<Boolean>()
    val exercisesButtonTextState = MutableLiveData<String>()
    var exercisesProgress =  MutableLiveData<ExercisesState>(ExercisesState.START_SCREEN)
    var toolbarDividerState = SingleLiveEvent<Boolean>()
    var buttonDividerState = SingleLiveEvent<Boolean>()

    lateinit var exerciseRequestData: ExerciseRequestData
    private val exercisesToRetry = ArrayList<ExerciseUiModel>()

    var chapterPartNumber: Int = 0
    var arrayIndex = 0
    private var exerciseIndex = 0

    lateinit var exerciseUiModel: ExerciseUiModel
    lateinit var exercisesResponseData: ExercisesResponseData
    private lateinit var exercises: List<ExerciseUiModel>
    private val exercisesInteractor = ExercisesModule.getExercisesInteractorImpl()
    private val exerciseResultInteractor = ExerciseResultModule.getExerciseResultInteractorImpl()

    private var falseAnswers = 0
    private var seconds = 0
    private var secondsCounter: Timer? = null

    private var job: Job? = null

    var selectedPos = -1

    fun getExercises(chapterPartNumber: Int){
        job = viewModelScope.launchExt(job){
            when(val status = exercisesInteractor.getExercises(chapterPartNumber)){
                is ExercisesStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                is ExercisesStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                is ExercisesStatus.Success -> {
                    exercisesState.postValue(true)
                    exercisesResponseData = status.result
                    exercises = ExercisesUiModelMapper().map(exercisesResponseData.exercises)
                    this@ExercisesViewModel.chapterPartNumber = chapterPartNumber
                }
            }
        }
    }

    fun setExercisesProgress(state: ExercisesState){
        exercisesProgress.value = state
    }

    fun showExercisesEndTextButton(show: Boolean){
        exercisesEndTextButtonState.value = show
    }

    fun setButtonText(text: String){
        exercisesButtonTextState.value = text
    }

    fun setButtonEnabled(enabled: Boolean){
        exercisesButtonEnabledState.value = enabled
    }

    fun showToolbarDivider(show: Boolean){
        toolbarDividerState.value = show
    }

    fun showButtonDivider(show: Boolean){
        buttonDividerState.value = show
    }

    fun initFragment(){
        selectedPos = -1
        answered.value = false
        snackbarState.value = null
        if(exerciseIndex == exercises.count{it is ExerciseUiModel.ExerciseUiModelExercise}){
            progressBarState.value = getProgressPercent() to true
        }else {
            exerciseUiModel = if (arrayIndex < exercises.size) {
                exercises[arrayIndex]
            } else {
                exercisesToRetry[0]
            }
            setButtonEnabled(exerciseUiModel is ExerciseUiModel.ExerciseUiModelScreen)
            val exerciseFragment = getFragmentToAdd(exerciseUiModel)
            navigationState.value = when (arrayIndex) {
                0 -> {
                    ExercisesNavigationState.FIRST
                }
                else -> {
                    ExercisesNavigationState.REPLACE
                }
            } to exerciseFragment
        }
    }


    fun checkExerciseResult(){
        answered.value = true
        job = viewModelScope.launchExt(job){
            if(exerciseUiModel is ExerciseUiModel.ExerciseUiModelExercise){
                when(val status = (exerciseUiModel as ExerciseUiModel.ExerciseUiModelExercise).exerciseNumber?.let {
                    exerciseResultInteractor.getExerciseResult(
                        it, exerciseRequestData)
                }) {
                    is ExerciseResultStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                    is ExerciseResultStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                    is ExerciseResultStatus.Success -> {
                        snackbarState.postValue(exerciseUiModel to status.result)
                        if (status.result.exerciseResult) {
                            exerciseIndex++

                            if (exerciseIndex == exercises.count { it is ExerciseUiModel.ExerciseUiModelExercise }) {
                                stopSecondsCounter()
                            }
                            progressBarState.value = getProgressPercent() to false
                        } else {
                            exercisesToRetry.addAll(exercises.filter{it.exerciseNumber == exerciseUiModel.exerciseNumber })
                        }

                        if (arrayIndex >= exercises.size) {
                            exercisesToRetry.removeAt(0)
                        }

                        if (arrayIndex == exercises.size - 1)
                            falseAnswers = exercisesToRetry.count { it is ExerciseUiModel.ExerciseUiModelExercise }
                        arrayIndex++
                    }
                }
            }else{
                if (arrayIndex >= exercises.size) {
                    exercisesToRetry.removeAt(0)
                }
                arrayIndex++
                initFragment()
            }
        }
    }

    private fun getFragmentToAdd(exerciseUiModel: ExerciseUiModel) =
        when(exerciseUiModel){
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType1UiModel -> ExerciseVariantsType1Fragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType2UiModel  -> ExerciseVariantsType2Fragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType3UiModel  -> ExerciseVariantsType3Fragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType4UiModel  -> ExerciseVariantsType4Fragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType5and6UiModel  -> ExerciseVariantsType5and6Fragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType7UiModel  -> ExerciseVariantsType7Fragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType8UiModel  -> ExerciseVariantsType8Fragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType9UiModel  -> ExerciseInputEquationFragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType10UiModel  -> ExerciseMissedNumberFragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType11UiModel  -> ExerciseInputCollectionFragment()
            is ExerciseUiModel.ExerciseUiModelScreen.ScreenType1UiModel -> ExerciseScreenType1()
            is ExerciseUiModel.ExerciseUiModelScreen.ScreenType2UiModel -> ExerciseScreenType2()
            is ExerciseUiModel.ExerciseUiModelScreen.ScreenType3UiModel -> ExerciseScreenType3()
        }


    private fun getProgressPercent(): Int = ((exerciseIndex/(exercises.count { it is ExerciseUiModel.ExerciseUiModelExercise }).toFloat())*100).toInt()

    private fun getAnswersPercent(): Int = (((getTrueAnswers()-getFalseAnswers())/getTrueAnswers().toFloat())*100).toInt()

    private fun getTrueAnswers(): Int = exercises.count { it is ExerciseUiModel.ExerciseUiModelExercise } - getFalseAnswers()

    private fun getFalseAnswers(): Int = falseAnswers

    fun getExercisesEndFragment(): Fragment{
        val fragment = ExercisesEndFragment()
        val bundle = Bundle()
        bundle.putInt("TRUE_ANSWERS", getTrueAnswers())
        bundle.putInt("FALSE_ANSWERS", getFalseAnswers())
        bundle.putInt("ANSWERS_PERCENT", getAnswersPercent())
        bundle.putInt("PROCESS_SECONDS", seconds)
        fragment.arguments = bundle
        return fragment
    }

    fun startSecondsCounter(){
        secondsCounter = Timer()
        secondsCounter?.schedule(object : TimerTask() {
            override fun run() {
                seconds++
                println("EXERCISE TIME IS $seconds SECONDS")
            }
        }, 1000, 1000)
    }

    fun stopSecondsCounter(){
        if(!secondsCounterIsStopped()) {
            secondsCounter!!.cancel()
            secondsCounter!!.purge()
            secondsCounter = null
            println("EXERCISE TIME STOP")
        }
    }

    fun secondsCounterIsStopped() = secondsCounter == null

    enum class ExercisesNavigationState{
        FIRST,
        REPLACE,
    }

    enum class ExercisesState{
        START_SCREEN,
        DESCRIPTION,
        EXERCISES
    }
}