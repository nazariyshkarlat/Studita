package com.example.studita.presentation.view_model

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.di.exercise.ExerciseResultModule
import com.example.studita.di.exercise.ExercisesModule
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.domain.entity.exercise.ExerciseResponseData
import com.example.studita.domain.interactor.ExerciseResultStatus
import com.example.studita.domain.interactor.ExercisesStatus
import com.example.studita.presentation.extensions.launchExt
import com.example.studita.presentation.fragments.ExercisesEndFragment
import com.example.studita.presentation.fragments.exercise.*
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.model.mapper.ExercisesUiModelMapper
import kotlinx.android.synthetic.main.exercise_layout.*
import kotlinx.coroutines.Job
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*
import kotlin.collections.ArrayList
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

class ExercisesViewModel : ViewModel(){

    val exercisesState = SingleLiveEvent<Boolean>()
    val exercisesEndTextButtonState = SingleLiveEvent<Boolean>()
    val navigationState = SingleLiveEvent<Pair<ExercisesNavigationState, Fragment>>()
    val progressBarState = SingleLiveEvent<Pair<Int, Boolean>>()
    val answered = SingleLiveEvent<Boolean>()
    val snackbarState = SingleLiveEvent<Pair<ExerciseUiModel, ExerciseResponseData>>()
    val errorState = SingleLiveEvent<Int>()
    val exercisesButtonState = SingleLiveEvent<Boolean>()

    lateinit var exerciseRequestData: ExerciseRequestData
    private val exercisesToRetry = ArrayList<Int>()

    private var exerciseIndex = 0
    private var score = 0

    private lateinit var results: List<ExerciseUiModel>
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
                    results = ExercisesUiModelMapper()
                        .map(status.result)
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

        if(score == results.size){
            score++
            progressBarState.value = getProgressBarPercent() to true
        }else {
            val exerciseUiModel = if (exerciseIndex < results.size) {
                results[exerciseIndex]
            } else {
                results[exercisesToRetry[0]]
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
        val exerciseUiModel =if(exerciseIndex < results.size) {
            results[exerciseIndex]
        }else{
            results[exercisesToRetry[0]]
        }
        job = viewModelScope.launchExt(job){
            when(val status = exerciseResultInteractor.getExerciseResult(exerciseUiModel.exerciseNumber, exerciseRequestData)){
                is ExerciseResultStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                is ExerciseResultStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                is ExerciseResultStatus.Success -> {
                    snackbarState.postValue(exerciseUiModel to status.result)
                    if (status.result.exerciseResult) {
                        score++
                        if(score == results.size)
                            stopSecondsCounter()
                        progressBarState.value =getProgressBarPercent() to false
                    } else {
                        if (exerciseIndex < results.size) {
                            exercisesToRetry.add(exerciseIndex)
                        }else {
                            exercisesToRetry.add(exercisesToRetry[0])
                        }
                    }
                    if(exerciseIndex >= results.size){
                        exercisesToRetry.removeAt(0)
                    }else
                        if(exerciseIndex == results.size-1)
                            falseAnswers = exercisesToRetry.size

                    exerciseIndex++
                }
            }
        }
    }

    private fun getFragmentToAdd(exerciseUiModel: ExerciseUiModel) =
        when(exerciseUiModel){
            is ExerciseUiModel.ExerciseUi1, is ExerciseUiModel.ExerciseUi2, is ExerciseUiModel.ExerciseUi5, is ExerciseUiModel.ExerciseUi7 -> ExerciseVariantsFragment()
            is ExerciseUiModel.ExerciseUi3 -> ExerciseInputFragment()
            is ExerciseUiModel.ExerciseUi4 -> ExerciseCharacterFragment()
            is ExerciseUiModel.ExerciseUi6 -> ExerciseInputEquationFrament()
            is ExerciseUiModel.ExerciseUi8, is ExerciseUiModel.ExerciseUi9 -> ExerciseInputCollectionFragment()
        }

    private fun getProgressBarPercent(): Int =  ((score / results.size.toFloat())*100).toInt()

    private fun getAnswersPercent(): Int = ((score/(exerciseIndex+1).toFloat())*100).toInt()

    private fun getTrueAnswers(): Int = results.size - getFalseAnswers()

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

    enum class ExercisesNavigationState{
        FIRST,
        REPLACE,
    }
}