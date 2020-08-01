package com.example.studita.presentation.view_model

import android.app.Application
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.studita.R
import com.example.studita.di.data.CompleteExercisesModule
import com.example.studita.di.data.UserDataModule
import com.example.studita.di.data.UserStatisticsModule
import com.example.studita.di.data.exercise.ExerciseResultModule
import com.example.studita.di.data.exercise.ExercisesModule
import com.example.studita.domain.entity.*
import com.example.studita.domain.entity.exercise.ExerciseData
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.domain.entity.exercise.ExerciseResponseData
import com.example.studita.domain.entity.exercise.ExercisesResponseData
import com.example.studita.domain.interactor.*
import com.example.studita.presentation.fragments.ChapterBottomSheetFragment
import com.example.studita.presentation.fragments.HomeFragment
import com.example.studita.presentation.fragments.exercises.ExercisesBonusFragment
import com.example.studita.presentation.fragments.exercises.ExercisesEndFragment
import com.example.studita.presentation.fragments.exercises.description.*
import com.example.studita.presentation.fragments.exercises.exercise.*
import com.example.studita.presentation.fragments.exercises.screen.ExerciseScreenType1
import com.example.studita.presentation.fragments.exercises.screen.ExerciseScreenType2
import com.example.studita.presentation.fragments.exercises.screen.ExerciseScreenType3
import com.example.studita.presentation.fragments.exercises.screen.ExerciseScreenType4
import com.example.studita.presentation.model.ExerciseUiModel
import com.example.studita.presentation.model.toUiModel
import com.example.studita.utils.*
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ExercisesViewModel(val app: Application) : AndroidViewModel(app){

    val exercisesState = SingleLiveEvent<Boolean>()
    val endButtonState = SingleLiveEvent<Boolean>()
    val navigationState = SingleLiveEvent<Pair<ExercisesNavigationState, Fragment>>()
    val progressBarVisibleState = MutableLiveData<Boolean>(false)
    val progressState = SingleLiveEvent<Pair<Float, Boolean>>()
    val answered = MutableLiveData<Boolean>(false)
    val snackbarState = SingleLiveEvent<Pair<ExerciseUiModel, ExerciseResponseData>?>()
    val toolbarProgressBarAnimEvent = SingleLiveEvent<Boolean>()
    val errorState = SingleLiveEvent<Int>()
    val showBadConnectionDialogAlertFragmentState = SingleLiveEvent<Boolean>()
    val buttonEnabledState = MutableLiveData<Boolean>()
    val buttonTextState = MutableLiveData<String>()
    val exercisesProgress =  MutableLiveData(ExercisesState.START_SCREEN)
    val exercisesBonusRemainingTimeState = MutableLiveData<Long>()
    val toolbarDividerState = SingleLiveEvent<Boolean>()
    val buttonDividerState = SingleLiveEvent<Boolean>()
    val transparentLayoutsAreVisibleState = MutableLiveData<Boolean>()
    val exerciseBonusResultState = MutableLiveData<Boolean>()
    val exerciseBonusNavigationState = SingleLiveEvent<Boolean>()
    val saveUserDataState = SingleLiveEvent<Pair<Boolean, UserDataData>>()

    lateinit var exerciseRequestData: ExerciseRequestData
    private val exercisesToRetry = ArrayList<ExerciseData>()

    private val waitingTime = 5000L

    var isBonusCompleted = true
    var isTraining = false
    var exerciseResultSuccess = false

    var chapterPartsCount = 0
    var chapterPartNumber = 0
    var chapterNumber = 0
    private var arrayIndex = 0
    private var bonusIndex = 0
    private var exerciseIndex = 0
    private var obtainedXP = 0

    lateinit var exerciseData: ExerciseData
    lateinit var exerciseUiModel: ExerciseUiModel
    lateinit var exercisesResponseData: ExercisesResponseData
    lateinit var exercises: List<ExerciseData>
    private var bonusExercises = emptyList<ExerciseData>()

    private val userStatisticsInteractor = UserStatisticsModule.getUserStatisticsInteractorImpl()
    private val userDataInteractor = UserDataModule.getUserDataInteractorImpl()
    private val completeExercisesInteractor = CompleteExercisesModule.getCompleteExercisesInteractorImpl()
    private val exercisesInteractor = ExercisesModule.getExercisesInteractorImpl()
    private val exerciseResultInteractor = ExerciseResultModule.getExerciseResultInteractorImpl()

    private var falseAnswers = 0
    private var correctBonusAnswers = 0
    private var seconds = 0L
    var bonusEndTime = 0L
    private var secondsCounter: Timer? = null

    private var job: Job? = null
    var waitingJob: Job? = null

    fun getExercises(chapterPartNumber: Int){
        job = viewModelScope.launchExt(job){
            when(val status = exercisesInteractor.getExercises(chapterPartNumber, PrefsUtils.isOfflineModeEnabled())){
                is ExercisesStatus.NoConnection -> exercisesState.postValue(false)
                is ExercisesStatus.ServiceUnavailable -> exercisesState.postValue(false)
                is ExercisesStatus.Success -> {

                    job = null
                    exercisesState.postValue(true)
                    exercisesResponseData = status.result

                    if(exercisesResponseData.exercises.any { it is ExerciseData.ExerciseDataScreen.ScreenType4Data && it.isBonusStart }) {
                        val bonusStartIndex = exercisesResponseData.exercises.indexOfFirst { it is ExerciseData.ExerciseDataScreen.ScreenType4Data && it.isBonusStart }
                        isBonusCompleted = false

                        bonusExercises = exercisesResponseData.exercises.subList(bonusStartIndex, exercisesResponseData.exercises.size)
                        exercises = exercisesResponseData.exercises.subList(0,bonusStartIndex)
                    }else
                        exercises = exercisesResponseData.exercises

                    if(answered.value == true){
                        if(exerciseData == (getCurrentExerciseData() as ExerciseData.ExerciseDataExercise).copy()) {
                            exerciseData = getCurrentExerciseData()
                            checkExerciseResult()
                        }else
                            initFragment()
                    }
                }
            }
        }
    }

    private suspend fun completeExercises(completedExercisesData: CompletedExercisesData, oldUserDataData: UserDataData){
        if (completeExercisesInteractor.completeExercises(
                CompleteExercisesRequestData(
                    UserUtils.getUserIDTokenData(),
                    completedExercisesData
                )
            ) is CompleteExercisesStatus.Success
        ) {
            saveUserDataState.postValue(true to oldUserDataData)
        }
    }

    private fun saveObtainedExercisesResult(){
        GlobalScope.launch {
            val userDataStatus = userDataInteractor.getUserData(UserUtils.userData.userId, PrefsUtils.isOfflineModeEnabled())
            if(userDataStatus is UserDataStatus.Success) {
                val currentUserData = userDataStatus.result.copy()
                currentUserData.let {

                    obtainedXP = LevelUtils.getObtainedXP(
                        it,
                        getAnswersPercent(),
                        isTraining
                    )

                    val newLevelsCount = LevelUtils.getNewLevelsCount(it, obtainedXP)
                    val newLevelXP = LevelUtils.getNewLevelXP(it, obtainedXP)

                    it.currentLevel += newLevelsCount
                    it.currentLevelXP = newLevelXP

                    val daysDiff = TimeUtils.getCalendarDayCount(it.streakDatetime, Date())
                    if (daysDiff != 0L) {
                        if (it.streakDays == 0)
                            it.streakDays = 1
                        else if (daysDiff == 1L)
                            it.streakDays = it.streakDays + 1
                        it.streakDatetime = Date()
                    }

                    if (!isTraining) {
                        it.completedParts[chapterNumber - 1]++
                        it.todayCompletedExercises++

                        if (chapterPartNumber == chapterPartsCount)
                            ChapterBottomSheetFragment.snackbarShowReason =
                                ChapterBottomSheetFragment.Companion.SnackbarShowReason.CHAPTER_COMPLETED

                        if (getAnswersPercent() < 0.6F) {
                            ChapterBottomSheetFragment.snackbarShowReason =
                                if (ChapterBottomSheetFragment.snackbarShowReason == ChapterBottomSheetFragment.Companion.SnackbarShowReason.CHAPTER_COMPLETED)
                                    ChapterBottomSheetFragment.Companion.SnackbarShowReason.CHAPTER_COMPLETED_AND_BAD_RESULT
                                else
                                    ChapterBottomSheetFragment.Companion.SnackbarShowReason.BAD_RESULT
                        }
                        ChapterBottomSheetFragment.needsRefresh = true
                    }

                    HomeFragment.needsRefresh = true

                    withContext(Dispatchers.Main) {
                        UserUtils.userDataLiveData.value = it
                    }

                    saveUserData(it)
                    saveUserStatistics(
                        UserStatisticsRowData(
                            Date(),
                            obtainedXP,
                            seconds,
                            if (!isTraining) 1 else null,
                            if (isTraining) 1 else null,
                            null
                        )
                    )

                    completeExercises(
                        CompletedExercisesData(
                            chapterNumber,
                            chapterPartNumber,
                            getAnswersPercent(),
                            Date(),
                            seconds
                        ),
                        userDataStatus.result
                    )
                }
            }
        }
    }

    private fun saveUserData(userDataData: UserDataData){
        GlobalScope.launch {
            userDataInteractor.saveUserData(userDataData)
        }
    }
    private fun saveUserStatistics(userStatisticsRowData: UserStatisticsRowData){
        GlobalScope.launch {
            userStatisticsInteractor.saveUserStatistics(UserUtils.getUserIDTokenData(), userStatisticsRowData)
        }
    }

    fun animateProgressBarVisibility(isVisible: Boolean){
        toolbarProgressBarAnimEvent.value = isVisible
    }

    fun setExercisesProgress(state: ExercisesState){
        exercisesProgress.value = state
    }

    fun showExercisesEndTextButton(show: Boolean){
        endButtonState.value = show
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
        transparentLayoutsAreVisibleState.value = show
    }

    fun setProgressBarVisibility(isVisible: Boolean){
        progressBarVisibleState.value = isVisible
    }

    fun initFragment(){
        answered.value = false
        snackbarState.value = null
        if (exerciseIndex == getExercisesCount() && isBonusCompleted) {
            if(progressState.value?.second == false)
                progressState.value = 1F to true
        } else {

            if(exerciseIndex != 0 && isBonusScreen()) {
                animateProgressBarVisibility(false)
                bonusEndTime = seconds+(bonusExercises.first() as ExerciseData.ExerciseDataScreen.ScreenType4Data).bonusSeconds
                stopSecondsCounter()
                setExercisesProgress(ExercisesState.BONUS_SCREEN)
                exerciseData = bonusExercises.first()
                bonusIndex++
            }else {
                exerciseData = getCurrentExerciseData()
                setButtonEnabled(exerciseData is ExerciseData.ExerciseDataScreen)
            }

            exerciseUiModel = exerciseData.toUiModel(getApplication())
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

    fun initBonusFragment(): Fragment{

        if(wasLastBonusExercise()){
            return getExercisesBonusEndFragment()
        }

        answered.value = false
        exerciseBonusResultState.value = null
        exerciseData = getCurrentBonusExerciseData()
        exerciseUiModel = exerciseData.toUiModel(getApplication())
        bonusIndex++
        return getFragmentToAdd(exerciseUiModel)
    }

    private fun wasLastBonusExercise() = bonusIndex >= bonusExercises.size

    private fun getCurrentBonusExerciseData() = bonusExercises[bonusIndex]

    private fun getCurrentExerciseData() = if (arrayIndex < exercises.size) {
        exercises[arrayIndex]
    } else {
        exercisesToRetry[0]
    }

    private fun launchWaitingCoroutine(){
        waitingJob = viewModelScope.launchExt(waitingJob) {
            delay(waitingTime)
            showBadConnectionDialogAlertFragmentState.postValue(true)
        }
    }

    private fun isBonusScreen() = exerciseIndex == getExercisesCount() && bonusExercises.isNotEmpty()

    fun checkExerciseResult(){

        if((job == null || job?.isCompleted == true) && (waitingJob == null || waitingJob?.isCompleted == true)) {
            answered.value = true
            exerciseResultSuccess = false
            job = viewModelScope.launchExt(job) {
                if (exerciseUiModel is ExerciseUiModel.ExerciseUiModelExercise) {
                    launchWaitingCoroutine()

                    when (val status =
                        exerciseResultInteractor.getExerciseResult(
                            exerciseData as ExerciseData.ExerciseDataExercise,
                            exerciseRequestData,
                            (exerciseData as ExerciseData.ExerciseDataExercise).exerciseAnswer != null
                        )) {
                        is ExerciseResultStatus.NoConnection -> errorState.postValue(R.string.no_connection)
                        is ExerciseResultStatus.ServiceUnavailable -> errorState.postValue(R.string.server_unavailable)
                        is ExerciseResultStatus.Success -> {

                            exerciseResultSuccess = true

                            showBadConnectionDialogAlertFragmentState.postValue(false)
                            waitingJob?.cancel()

                            snackbarState.postValue(exerciseUiModel to status.result)
                            if (status.result.exerciseResult) {
                                exerciseIndex++

                                if (exerciseIndex == getExercisesCount()) {
                                    stopSecondsCounter()
                                    saveObtainedExercisesResult()
                                }

                                progressState.value = getProgressPercent() to false
                            } else {
                                exercisesToRetry.addAll(exercises.filter { it.exerciseNumber == exerciseUiModel.exerciseNumber })
                            }

                            if (arrayIndex >= exercises.size) {
                                exercisesToRetry.removeAt(0)
                            }

                            if (arrayIndex == exercises.size - 1) {
                                falseAnswers =
                                    exercisesToRetry.count { it is ExerciseData.ExerciseDataExercise }
                            }
                            arrayIndex++
                        }
                    }
                } else {
                    waitingJob?.cancel()
                    if (arrayIndex >= exercises.size)
                        exercisesToRetry.removeAt(0)
                    arrayIndex++
                    initFragment()
                }
            }
        }else if(showBadConnectionDialogAlertFragmentState.value == true){
            waitingJob?.cancel()
            showBadConnectionDialogAlertFragmentState.postValue(true)
        }
    }

    fun checkBonusResult() {
        answered.value = true
        val trueAnswer = (exerciseData as ExerciseData.ExerciseDataExercise).exerciseAnswer?.split(",")?.toSet() == exerciseRequestData.exerciseAnswer.split(",").toSet()
        if(trueAnswer)
            correctBonusAnswers++
        exerciseBonusResultState.value = trueAnswer

        viewModelScope.launch {
            delay(app.resources.getInteger(R.integer.bonus_exercises_delay).toLong())
            exerciseBonusNavigationState.value = true
        }
    }

    private fun getFragmentToAdd(exerciseUiModel: ExerciseUiModel) =
        when(exerciseUiModel){
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType1UiModel -> ExerciseVariantsType1Fragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType2UiModel  -> ExerciseVariantsType2Fragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType3UiModel -> ExerciseVariantsType3Fragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType4UiModel  -> ExerciseVariantsType4Fragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType5And6UiModel  -> ExerciseVariantsType5and6Fragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType7UiModel  -> ExerciseVariantsType7Fragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType8And12UiModel  -> ExerciseVariantsType8Fragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType9UiModel  -> ExerciseInputEquationFragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType10UiModel  -> ExerciseMissedNumberFragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType11UiModel  -> ExerciseInputCollectionFragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType13UiModel  -> ExerciseVariantsType13Fragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType14UiModel  -> ExerciseVariantsType2Fragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType15UiModel -> ExerciseVariantsType15Fragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType16UiModel -> ExerciseMissedCharacterFragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType17UiModel -> ExerciseVariantsType17Fragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType18UiModel -> ExerciseVariantsType18Fragment()
            is ExerciseUiModel.ExerciseUiModelScreen.ScreenType1UiModel -> ExerciseScreenType1()
            is ExerciseUiModel.ExerciseUiModelScreen.ScreenType2UiModel -> ExerciseScreenType2()
            is ExerciseUiModel.ExerciseUiModelScreen.ScreenType3UiModel -> ExerciseScreenType3()
            is ExerciseUiModel.ExerciseUiModelScreen.ScreenType4UiModel -> ExerciseScreenType4()
        }

    fun getDescriptionFragment(): ExercisesDescriptionFragment {
        return when(chapterPartNumber){
            1 -> ExercisesDescription1Fragment()
            2 -> ExercisesDescription2Fragment()
            4,5 -> ExercisesDescriptionPureFragment()
            7 -> ExercisesDescription7Fragment()
            9 -> ExercisesDescription9Fragment()
            10 -> ExercisesDescription10Fragment()
            else -> throw IOException("Unknown chapter part number")
        }
    }


    private fun getProgressPercent(): Float = exerciseIndex/getExercisesCount().toFloat()

    private fun getAnswersPercent(): Float =(getExercisesCount()-getFalseAnswers())/getExercisesCount().toFloat()

    private fun getTrueAnswers(): Int = getExercisesCount()- getFalseAnswers()

    private fun getFalseAnswers(): Int = falseAnswers

    private fun getExercisesCount() = exercises.count { it is ExerciseData.ExerciseDataExercise }

    fun getExercisesEndFragment(oldUserDataData: UserDataData) =
        ExercisesEndFragment().apply {
            arguments = bundleOf("TRUE_ANSWERS" to getTrueAnswers(),
                "FALSE_ANSWERS" to getFalseAnswers(),
                "ANSWERS_PERCENT" to getAnswersPercent(),
                "OBTAINED_XP" to obtainedXP,
                "PROCESS_SECONDS" to seconds,
                "OLD_USER_DATA" to Gson().toJson(oldUserDataData)
            )
        }

    fun getExercisesBonusEndFragment() =
        ExercisesBonusEndScreenFragment().apply {
            arguments = bundleOf("OBTAINED_XP" to LevelUtils.getObtainedExercisesBonusXP(correctBonusAnswers),
                "CORRECT_ANSWERS_COUNT" to  correctBonusAnswers)
        }

    fun startSecondsCounter(){
        secondsCounter = Timer()
        secondsCounter?.schedule(object : TimerTask() {
            override fun run() {
                seconds++
                if(bonusEndTime != 0L)
                    exercisesBonusRemainingTimeState.postValue(bonusEndTime-seconds)
                else
                    exercisesBonusRemainingTimeState.postValue(15)
                Log.d("EXERCISES", "TIME IS $seconds SECONDS")
            }
        }, 1000, 1000)
    }

    fun stopSecondsCounter(){
        if(!secondsCounterIsStopped()) {
            secondsCounter!!.cancel()
            secondsCounter!!.purge()
            secondsCounter = null
            Log.d("EXERCISES", "TIME STOP")
        }
    }

    fun secondsCounterIsStopped() = secondsCounter == null

    fun cancelExercisesJob(){
        job?.cancel()
    }

    fun startExercisesBonus() {
        navigationState.value =
            ExercisesNavigationState.NAVIGATE to ExercisesBonusFragment()
    }

    enum class ExercisesNavigationState{
        FIRST,
        REPLACE,
        NAVIGATE
    }

    enum class ExercisesState{
        START_SCREEN,
        DESCRIPTION,
        EXERCISES,
        BONUS_SCREEN
    }
}