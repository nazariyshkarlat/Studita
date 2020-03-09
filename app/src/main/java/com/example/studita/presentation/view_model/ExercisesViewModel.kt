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
import com.example.studita.presentation.extensions.launchExt
import com.example.studita.presentation.fragments.ExercisesEndFragment
import com.example.studita.presentation.fragments.exercise.*
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
    val exercisesButtonState = MutableLiveData<Boolean>()
    var exercisesProgress: ExercisesState = ExercisesState.START_PAGE
    var toolbarDividerState = SingleLiveEvent<Boolean>()
    var buttonDividerState = SingleLiveEvent<Boolean>()

    lateinit var exerciseRequestData: ExerciseRequestData
    private val exercisesToRetry = ArrayList<Int>()

    private var exerciseIndex = 0
    private var score = 0

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

    init{
        getExercises(1)
    }

    private fun getExercises(chapterPartNumber: Int){
        job = viewModelScope.launchExt(job){
            exercisesState.postValue(false)
            when(val status = exercisesInteractor.getExercises(chapterPartNumber)){
                is ExercisesStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                is ExercisesStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                is ExercisesStatus.Success -> {
                    exercisesState.postValue(true)
                    exercisesResponseData = status.result
                    exercises = ExercisesUiModelMapper().map(exercisesResponseData.exercises)
                    }
            }
        }
    }

    fun showExercisesEndTextButton(show: Boolean){
        exercisesEndTextButtonState.value = show
    }

    fun setButtonEnabled(enabled: Boolean){
        exercisesButtonState.value = enabled
    }

    fun initFragment(){

        selectedPos = -1
        answered.value = false
        snackbarState.value = null

        if(score == exercises.size){
            score++
            progressBarState.value = getProgressBarPercent() to true
        }else {
            val exerciseUiModel = if (exerciseIndex < exercises.size) {
                exercises[exerciseIndex]
            } else {
                exercises[exercisesToRetry[0]]
            }
            val exerciseFragment = getFragmentToAdd(exerciseUiModel)
            val bundle = Bundle()
            bundle.putParcelable("EXERCISE", exerciseUiModel)
            exerciseFragment.arguments = bundle
            navigationState.value = when (exerciseIndex) {
                0 -> ExercisesNavigationState.FIRST
                else -> {
                    ExercisesNavigationState.REPLACE
                }
            } to exerciseFragment
        }
    }


    fun checkExerciseResult(){
        answered.value = true
        exerciseUiModel =if(exerciseIndex < exercises.size) {
            exercises[exerciseIndex]
        }else{
            exercises[exercisesToRetry[0]]
        }
        job = viewModelScope.launchExt(job){
            when(val status = exerciseResultInteractor.getExerciseResult(exerciseUiModel.exerciseNumber, exerciseRequestData)){
                is ExerciseResultStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                is ExerciseResultStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                is ExerciseResultStatus.Success -> {
                    snackbarState.postValue(exerciseUiModel to status.result)
                    if (status.result.exerciseResult) {
                        score++
                        if(score == exercises.size)
                            stopSecondsCounter()
                        progressBarState.value =getProgressBarPercent() to false
                    } else {
                        if (exerciseIndex < exercises.size) {
                            exercisesToRetry.add(exerciseIndex)
                        }else {
                            exercisesToRetry.add(exercisesToRetry[0])
                        }
                    }
                    if(exerciseIndex >= exercises.size){
                        exercisesToRetry.removeAt(0)
                    }else
                        if(exerciseIndex == exercises.size-1)
                            falseAnswers = exercisesToRetry.size

                    exerciseIndex++
                }
            }
        }
    }

    private fun getFragmentToAdd(exerciseUiModel: ExerciseUiModel) =
        when(exerciseUiModel){
            is ExerciseUiModel.ExerciseUi1, is ExerciseUiModel.ExerciseUi2, is ExerciseUiModel.ExerciseUi5, is ExerciseUiModel.ExerciseUi7 -> ExerciseVariantsTitleFragment()
            is ExerciseUiModel.ExerciseUi3 -> ExerciseInputFragment()
            is ExerciseUiModel.ExerciseUi4 -> ExerciseCharacterFragment()
            is ExerciseUiModel.ExerciseUi6 -> ExerciseInputEquationFragment()
            is ExerciseUiModel.ExerciseUi8, is ExerciseUiModel.ExerciseUi9 -> ExerciseInputCollectionFragment()
        }

    private fun getProgressBarPercent(): Int =  ((score / exercises.size.toFloat())*100).toInt()

    private fun getAnswersPercent(): Int = ((score/(exerciseIndex+1).toFloat())*100).toInt()

    private fun getTrueAnswers(): Int = exercises.size - getFalseAnswers()

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
            }
        }, 1000, 1000)
    }

    fun stopSecondsCounter(){
        if(secondsCounter != null) {
            secondsCounter!!.cancel()
            secondsCounter!!.purge()
            secondsCounter = null
        }
    }

    fun showToolbarDivider(show: Boolean){
        toolbarDividerState.value = show
    }

    fun showButtonDivider(show: Boolean){
        buttonDividerState.value = show
    }

    enum class ExercisesNavigationState{
        FIRST,
        REPLACE,
    }

    enum class ExercisesState{
        START_PAGE,
        DESCRIPTION,
        EXERCISES
    }
}