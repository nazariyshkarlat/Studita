package com.example.studita.presentation.view_model

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.di.DatabaseModule
import com.example.studita.di.data.ObtainedExerciseDataModule
import com.example.studita.di.data.UserDataModule
import com.example.studita.di.data.exercise.ExerciseResultModule
import com.example.studita.di.data.exercise.ExercisesModule
import com.example.studita.domain.entity.ObtainedExerciseDataData
import com.example.studita.domain.entity.UserDataData
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.domain.entity.exercise.ExerciseResponseData
import com.example.studita.domain.entity.exercise.ExercisesResponseData
import com.example.studita.domain.interactor.ExerciseResultStatus
import com.example.studita.domain.interactor.ExercisesStatus
import com.example.studita.domain.interactor.SaveObtainedExerciseDataStatus
import com.example.studita.presentation.fragments.ChapterBottomSheetFragment
import com.example.studita.presentation.fragments.HomeFragment
import com.example.studita.presentation.utils.launchExt
import com.example.studita.presentation.fragments.exercises.ExercisesEndFragment
import com.example.studita.presentation.fragments.exercises.exercise.*
import com.example.studita.presentation.fragments.exercises.screen.ExerciseScreenType1
import com.example.studita.presentation.fragments.exercises.screen.ExerciseScreenType2
import com.example.studita.presentation.fragments.exercises.screen.ExerciseScreenType3
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.model.mapper.ExercisesUiModelMapper
import com.example.studita.presentation.utils.LevelUtils
import com.example.studita.presentation.utils.TimeUtils
import com.example.studita.presentation.utils.UserUtils
import com.example.studita.presentation.utils.UserUtils.oldUserData
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ExercisesViewModel : ViewModel(){

    val exercisesState = SingleLiveEvent<Boolean>()
    val endTextButtonState = SingleLiveEvent<Boolean>()
    val navigationState = SingleLiveEvent<Pair<ExercisesNavigationState, Fragment>>()
    val progressBarState = SingleLiveEvent<Pair<Float, Boolean>>()
    val answered = MutableLiveData<Boolean>()
    val snackbarState = SingleLiveEvent<Pair<ExerciseUiModel, ExerciseResponseData>?>()
    val errorState = SingleLiveEvent<Int>()
    val buttonEnabledState = MutableLiveData<Boolean>()
    val buttonTextState = MutableLiveData<String>()
    var exercisesProgress =  MutableLiveData(ExercisesState.START_SCREEN)
    var toolbarDividerState = SingleLiveEvent<Boolean>()
    var barsState = SingleLiveEvent<Boolean>()
    var buttonDividerState = SingleLiveEvent<Boolean>()
    var saveObtainedExerciseDataState = SingleLiveEvent<Boolean>()

    lateinit var exerciseRequestData: ExerciseRequestData
    private val exercisesToRetry = ArrayList<ExerciseUiModel>()

    var isTraining = false
    var chapterPartsCount = 0
    var chapterPartNumber = 0
    var chapterNumber = 0
    var arrayIndex = 0
    private var exerciseIndex = 0
    private var obtainedXP = 0

    lateinit var exerciseUiModel: ExerciseUiModel
    lateinit var exercisesResponseData: ExercisesResponseData
    private lateinit var exercises: List<ExerciseUiModel>

    private val userDataInteractor = UserDataModule.getUserDataInteractorImpl()
    private val exercisesInteractor = ExercisesModule.getExercisesInteractorImpl()
    private val exerciseResultInteractor = ExerciseResultModule.getExerciseResultInteractorImpl()
    private val obtainedExerciseDataInteractor = ObtainedExerciseDataModule.getObtainedExerciseDataInteractorImpl()

    private var falseAnswers = 0
    private var seconds = 0L
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
                }
            }
        }
    }

    private fun saveUserData(userDataData: UserDataData){
        viewModelScope.launch {
            userDataInteractor.saveUserData(userDataData)
        }
    }

    private fun saveObtainedExercisesResult(){
        UserUtils.userData?.let {

            oldUserData = it.copy()

            obtainedXP = LevelUtils.getObtainedXP(
                it,
                getAnswersPercent(),
                isTraining
            )

            val newLevelsCount = LevelUtils.getNewLevelsCount(it, obtainedXP)
            val newLevelXP = LevelUtils.getNewLevelXP(it, obtainedXP)

            val data = ObtainedExerciseDataData(
                training = isTraining,
                obtainedXP = obtainedXP,
                obtainedTime = seconds,
                newLevelsCount = newLevelsCount,
                newLevelXP = LevelUtils.getNewLevelXP(it, obtainedXP),
                chapterNumber = chapterNumber
            )

            it.currentLevel += newLevelsCount
            it.currentLevelXP = newLevelXP

            val daysDiff = TimeUtils.getCalendarDayCount(it.streakDate, Date())
            if(daysDiff != 0L){
                if(it.streakDays == 0)
                    it.streakDays = 1
                else if(daysDiff == 1L)
                    it.streakDays = it.streakDays+1
                it.streakDate = Date()
            }

            if(!isTraining) {
                it.completedParts[chapterNumber - 1] = chapterPartNumber

                if(chapterPartNumber == chapterPartsCount)
                    ChapterBottomSheetFragment.snackbarShowReason = ChapterBottomSheetFragment.Companion.SnackbarShowReason.CHAPTER_COMPLETED

                if(getAnswersPercent() < 0.6F){
                    ChapterBottomSheetFragment.snackbarShowReason =
                        if(ChapterBottomSheetFragment.snackbarShowReason == ChapterBottomSheetFragment.Companion.SnackbarShowReason.CHAPTER_COMPLETED)
                            ChapterBottomSheetFragment.Companion.SnackbarShowReason.CHAPTER_COMPLETED_AND_BAD_RESULT
                        else
                            ChapterBottomSheetFragment.Companion.SnackbarShowReason.BAD_RESULT
                }
                ChapterBottomSheetFragment.needsRefresh = true
            }

            HomeFragment.needsRefresh = true

            saveUserData(it)

            viewModelScope.launch{
                UserUtils.getUserTokenIdData()?.let { it1 ->
                    if(obtainedExerciseDataInteractor.saveObtainedData(
                        it1,
                        data
                    ) is SaveObtainedExerciseDataStatus.Success){
                        saveObtainedExerciseDataState.postValue(true)
                    }
                }
            }
        }
    }

    fun setExercisesProgress(state: ExercisesState){
        exercisesProgress.value = state
    }

    fun showExercisesEndTextButton(show: Boolean){
        endTextButtonState.value = show
    }

    fun setButtonText(text: String){
        buttonTextState.value = text
    }

    fun setButtonEnabled(enabled: Boolean){
        buttonEnabledState.value = enabled
    }

    fun showToolbarDivider(show: Boolean){
        toolbarDividerState.value = show
    }

    fun showButtonDivider(show: Boolean){
        buttonDividerState.value = show
    }

    fun showBars(show: Boolean){
        barsState.value = show
    }

    fun initFragment(){
        selectedPos = -1
        answered.value = false
        snackbarState.value = null
        if (exerciseIndex == getExercisesCount()) {
            if(progressBarState.value?.second == false)
                progressBarState.value = 1F to true
        } else {
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
                                saveObtainedExercisesResult()
                            }

                            progressBarState.value = getProgressPercent() to false
                        } else {
                            exercisesToRetry.addAll(exercises.filter{it.exerciseNumber == exerciseUiModel.exerciseNumber })
                        }

                        if (arrayIndex >= exercises.size) {
                            exercisesToRetry.removeAt(0)
                        }

                        if (arrayIndex == exercises.size - 1) {
                            falseAnswers = exercisesToRetry.count { it is ExerciseUiModel.ExerciseUiModelExercise }
                        }
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


    private fun getProgressPercent(): Float = exerciseIndex/getExercisesCount().toFloat()

    private fun getAnswersPercent(): Float =(getExercisesCount()-getFalseAnswers())/getExercisesCount().toFloat()

    private fun getTrueAnswers(): Int = getExercisesCount()- getFalseAnswers()

    private fun getFalseAnswers(): Int = falseAnswers

    private fun getExercisesCount() = exercises.count { it is ExerciseUiModel.ExerciseUiModelExercise }

    fun getExercisesEndFragment(): Fragment{
        val fragment =
            ExercisesEndFragment()
        val bundle = Bundle()
        bundle.putInt("TRUE_ANSWERS", getTrueAnswers())
        bundle.putInt("FALSE_ANSWERS", getFalseAnswers())
        bundle.putFloat("ANSWERS_PERCENT", getAnswersPercent())
        bundle.putInt("OBTAINED_XP", obtainedXP)
        bundle.putLong("PROCESS_SECONDS", seconds)
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
        REPLACE
    }

    enum class ExercisesState{
        START_SCREEN,
        DESCRIPTION,
        EXERCISES
    }
}