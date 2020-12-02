package com.studita.presentation.view_model

import android.app.Application
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studita.R
import com.studita.di.data.CompleteExercisesModule
import com.studita.di.data.UserDataModule
import com.studita.di.data.UserStatisticsModule
import com.studita.di.data.exercise.ExerciseResultModule
import com.studita.di.data.exercise.ExercisesModule
import com.studita.domain.entity.CompleteExercisesRequestData
import com.studita.domain.entity.CompletedExercisesData
import com.studita.domain.entity.UserDataData
import com.studita.domain.entity.UserStatisticsRowData
import com.studita.domain.entity.exercise.ExerciseData
import com.studita.domain.entity.exercise.ExerciseRequestData
import com.studita.domain.entity.exercise.ExerciseResponseData
import com.studita.domain.entity.exercise.ExercisesResponseData
import com.studita.domain.interactor.CompleteExercisesStatus
import com.studita.domain.interactor.ExerciseResultStatus
import com.studita.domain.interactor.ExercisesStatus
import com.studita.domain.interactor.UserDataStatus
import com.studita.presentation.fragments.bottom_sheets.ChapterBottomSheetFragment
import com.studita.presentation.fragments.exercises.ExercisesBonusEndScreenFragment
import com.studita.presentation.fragments.exercises.ExercisesBonusFragment
import com.studita.presentation.fragments.exercises.ExercisesEndFragment
import com.studita.presentation.fragments.exercises.description.*
import com.studita.presentation.fragments.exercises.exercise_fragments.*
import com.studita.presentation.fragments.exercises.explanation.ExerciseExplanation1Fragment
import com.studita.presentation.fragments.exercises.screen.*
import com.studita.presentation.model.ExerciseUiModel
import com.studita.presentation.model.toUiModel
import com.studita.utils.*
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ExercisesViewModel(val app: Application, val chapterPartNumber: Int) : ViewModel() {

    val exercisesState = MutableLiveData<Boolean>()
    val endButtonState = SingleLiveEvent<Boolean>()
    val navigationState = SingleLiveEvent<Pair<ExercisesNavigationState, Fragment>>()
    val progressBarVisibleState = MutableLiveData<Boolean>(false)
    val progressState = SingleLiveEvent<Pair<Float, Boolean>>()
    val answered = MutableLiveData<Boolean>(false)
    val snackbarState = SingleLiveEvent<Pair<ExerciseUiModel, ExerciseResponseData>?>()
    val toolbarProgressBarAnimEvent = SingleLiveEvent<Boolean>()
    val showBadConnectionDialogAlertFragmentState = SingleLiveEvent<Boolean>()
    val buttonEnabledState = MutableLiveData<Boolean>()
    val buttonTextState = MutableLiveData<String>()
    val exercisesProgress = MutableLiveData(ExercisesState.START_SCREEN)
    val exercisesBonusRemainingTimeState = MutableLiveData<Long>()
    val toolbarDividerState = SingleLiveEvent<Boolean>()
    val buttonDividerState = SingleLiveEvent<Boolean>()
    val transparentLayoutsAreVisibleState = MutableLiveData<Boolean>()
    val exerciseBonusResultState = MutableLiveData<Boolean>()
    val selectVariantEvent = SingleLiveEvent<Int>()
    val exerciseBonusNavigationState = SingleLiveEvent<Boolean>()
    val saveUserDataState = SingleLiveEvent<Pair<Boolean, UserDataData>>()
    val loadScreenBadConnectionState = MutableLiveData<Boolean>()

    lateinit var exerciseRequestData: ExerciseRequestData

    private val connectionTimeout = 3000L

    var isBonusCompleted = true
    var isTraining = false
    var exercisesAreCompletedAndNoBonus = false
    var exercisesAreCompleted = false
    private var exerciseResultSuccess = false
    var timeCounterIsPaused = true
    var feedbackWasSent = false

    var chapterPartsInChapterCount = 0
    var chapterPartInChapterNumber = 0
    var chapterNumber = 0
    var exerciseCountToSelect = 0
    var exercisesInChapterCount = 0
    private var arrayIndex = 0
    private var bonusIndex = 0
    var exerciseIndex = 0
    private var obtainedXP = 0
    var chapterName: String? = null

    lateinit var exerciseData: ExerciseData
    lateinit var exerciseUiModel: ExerciseUiModel
    lateinit var exercisesResponseData: ExercisesResponseData
    lateinit var exercises: ArrayList<ExerciseData>
    private var bonusExercises = emptyList<ExerciseData>()
    private val exercisesToRetry = ArrayList<ExerciseData>()

    private val userStatisticsInteractor = UserStatisticsModule.getUserStatisticsInteractorImpl()
    private val userDataInteractor = UserDataModule.getUserDataInteractorImpl()
    private val completeExercisesInteractor =
        CompleteExercisesModule.getCompleteExercisesInteractorImpl()
    private val exercisesInteractor = ExercisesModule.getExercisesInteractorImpl()
    private val exerciseResultInteractor = ExerciseResultModule.getExerciseResultInteractorImpl()

    private var falseAnswers = 0
    var correctBonusAnswers = 0
    private var seconds = 0L
    var bonusEndTime = 0L
    private var secondsCounter: Timer? = null

    var saveObtainedExercisesDataJob: Job? = null
    var badConnectionJob: Job? = null
    private var getExercisesJob: Job? = null
    private var checkExerciseResultJob: Job? = null

    init {
        getExercises()
    }

    fun getExercises() {

        runBadConnectionCoroutine()

        getExercisesJob = viewModelScope.launchExt(getExercisesJob) {

            when (val status = exercisesInteractor.getExercises(
                chapterPartNumber,
                PrefsUtils.isOfflineModeEnabled()
            )) {
                is ExercisesStatus.NoConnection -> loadScreenBadConnectionState.value = true
                is ExercisesStatus.ServiceUnavailable ->loadScreenBadConnectionState.value = true
                is ExercisesStatus.Success -> {
                    getExercisesJob = null
                    badConnectionJob?.cancel()
                    exercisesState.value = true
                    exercisesResponseData = status.result

                    if (exercisesResponseData.exercises.any { it is ExerciseData.ExerciseDataScreen.ScreenType4Data && it.isBonusStart }) {
                        isBonusCompleted = false
                        val splitExercises = getSplitExercisesAndBonuses()
                        exercises = ArrayList(splitExercises.first)
                        bonusExercises = ArrayList(splitExercises.second)
                    } else
                        exercises = ArrayList(exercisesResponseData.exercises)

                    if (answered.value == true) {
                        val oldExercisesToRetry: ArrayList<ExerciseData> = exercisesToRetry.toMutableList() as ArrayList<ExerciseData>
                        exercisesToRetry.clear()

                        oldExercisesToRetry.forEach {old->
                            exercisesToRetry.add(exercises.first { it.exerciseNumber ==  old.exerciseNumber})
                        }

                        if ((exerciseData  as ExerciseData.ExerciseDataExercise).copy() == (getCurrentExerciseData() as ExerciseData.ExerciseDataExercise).copy()) {
                            exerciseData = getCurrentExerciseData()
                            checkExerciseResult()
                        } else
                            initFragment()
                    }
                }
            }
        }
    }

    private fun runBadConnectionCoroutine() {

        badConnectionJob = viewModelScope.launch(Dispatchers.Main) {
            delay(connectionTimeout)

            if(loadScreenBadConnectionState.value == null)
                loadScreenBadConnectionState.value = true
        }

    }

    private suspend fun completeExercises(
        completedExercisesData: CompletedExercisesData,
        oldUserDataData: UserDataData,
    ) {
        when {
            completeExercisesInteractor.completeExercises(
                CompleteExercisesRequestData(
                    UserUtils.getUserIDTokenData(),
                    completedExercisesData
                )
            ) is CompleteExercisesStatus.Success -> {
                if(!PrefsUtils.isOfflineModeEnabled())
                    saveUserDataState.value = true to oldUserDataData
            }else -> {
            if(!PrefsUtils.isOfflineModeEnabled() && showBadConnectionDialogAlertFragmentState.value != true)
                showBadConnectionDialogAlertFragmentState.value = true
        }
        }
    }

    fun saveObtainedExercisesResult() {
        timeCounterIsPaused = true
        saveObtainedExercisesDataJob = GlobalScope.launchExt(saveObtainedExercisesDataJob) {
            val userDataStatus = userDataInteractor.getUserData(
                UserUtils.userData.userId,
                PrefsUtils.isOfflineModeEnabled(),
                true
            )

            val data = CompletedExercisesData(
                chapterNumber,
                chapterPartInChapterNumber,
                getAnswersPercent(),
                Date(),
                seconds,
                correctBonusAnswers
            )

            if (userDataStatus is UserDataStatus.Success) {
                val currentUserData = userDataStatus.result.copy()
                currentUserData.let {

                    obtainedXP = LevelUtils.getObtainedXP(
                        it,
                        getAnswersPercent(),
                        isTraining,
                        correctBonusAnswers
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

                        if (chapterPartInChapterNumber == chapterPartsInChapterCount) {
                            PrefsUtils.makeCompletedChapterDialogWasNotShown(
                                exercisesInChapterCount,
                                chapterName!!
                            )
                            ChapterBottomSheetFragment.needsDismiss = true
                        }

                        if (getAnswersPercent() < 0.6F) {
                            ChapterBottomSheetFragment.snackbarShowReason = ChapterBottomSheetFragment.Companion.SnackbarShowReason.BAD_RESULT
                        }
                        ChapterBottomSheetFragment.needsRefresh = true
                    }


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

                    if(PrefsUtils.isOfflineModeEnabled()) {
                        completeExercisesInteractor.addLocalCompletedExercises(data)
                        saveUserDataState.value = true to userDataStatus.result
                    }

                    completeExercises(
                        data,
                        userDataStatus.result
                    )
                }
            }else{
                showBadConnectionDialogAlertFragmentState.value = true
            }
        }
    }

    private fun saveUserData(userDataData: UserDataData) {
        GlobalScope.launch(Dispatchers.Main) {
            userDataInteractor.saveUserData(userDataData)
        }
    }

    private fun saveUserStatistics(userStatisticsRowData: UserStatisticsRowData) {
        GlobalScope.launch(Dispatchers.Main) {
            userStatisticsInteractor.saveUserStatistics(
                UserUtils.getUserIDTokenData(),
                userStatisticsRowData
            )
        }
    }

    fun animateProgressBarVisibility(isVisible: Boolean) {
        toolbarProgressBarAnimEvent.value = isVisible
    }

    fun setExercisesProgress(state: ExercisesState) {
        exercisesProgress.value = state
    }

    fun showExercisesEndTextButton(show: Boolean) {
        endButtonState.value = show
    }

    fun setButtonText(text: String) {
        buttonTextState.value = text
    }

    fun setButtonEnabled(enabled: Boolean) {
        buttonEnabledState.value = enabled
    }

    fun showToolbarDivider(show: Boolean) {
        toolbarDividerState.value = show
    }

    fun showButtonDivider(show: Boolean) {
        buttonDividerState.value = show
    }

    fun showTransparentLayouts(show: Boolean) {
        transparentLayoutsAreVisibleState.value = show
    }

    fun setProgressBarVisibility(isVisible: Boolean) {
        progressBarVisibleState.value = isVisible
    }

    fun initFragment() {
        answered.value = false
        feedbackWasSent = false
        snackbarState.value = null
        if (exercisesAreCompleted && isBonusCompleted) {
            endExercises()
        } else {
            if (exerciseIndex != 0 && isBonusScreen()) {
                timeCounterIsPaused = true
                animateProgressBarVisibility(false)
                bonusEndTime =
                    seconds + (bonusExercises.first() as ExerciseData.ExerciseDataScreen.ScreenType4Data).bonusSeconds
                stopSecondsCounter()
                setExercisesProgress(ExercisesState.BONUS_SCREEN)
                exerciseData = bonusExercises.first()
                bonusIndex++
            } else {
                exerciseData = getCurrentExerciseData()
                if (exerciseData is ExerciseData.ExerciseExplanationData) {
                    showTransparentLayouts(true)
                    setExercisesProgress(ExercisesState.EXPLANATION)
                }
                setButtonEnabled(exerciseData !is ExerciseData.ExerciseDataExercise)
            }
            if(!isInputExercise()){
                exerciseCountToSelect = getVariantsToSelectCount()
            }

            exerciseUiModel = exerciseData.toUiModel(app)
            val exerciseFragment = getFragmentToAdd(exerciseUiModel)
            viewModelScope.launch(Dispatchers.Main) {
                navigationState.value = when {
                    arrayIndex == 0 -> {
                        ExercisesNavigationState.FIRST
                    }
                    exerciseData is ExerciseData.ExerciseExplanationData -> {
                        ExercisesNavigationState.NAVIGATE_IN_CONTAINER
                    }
                    else -> {
                        ExercisesNavigationState.REPLACE
                    }
                } to exerciseFragment
            }
        }
    }

    fun initBonusFragment(): Fragment {

        timeCounterIsPaused = false

        if (isLastBonusExercise()) {
            endBonusExercises()
            return getExercisesBonusEndFragment()
        }

        answered.value = false
        exerciseBonusResultState.value = null
        exerciseData = getCurrentBonusExerciseData()
        if(!isInputExercise()){
            exerciseCountToSelect = getVariantsToSelectCount()
        }
        exerciseUiModel = exerciseData.toUiModel(app)
        bonusIndex++
        return getFragmentToAdd(exerciseUiModel)
    }

    fun endBonusExercises() {
        isBonusCompleted = true
        timeCounterIsPaused = true
        stopSecondsCounter()
        saveObtainedExercisesResult()
    }

    private fun isLastBonusExercise() = bonusIndex >= bonusExercises.size

    private fun getCurrentBonusExerciseData() = bonusExercises[bonusIndex]

    private fun getCurrentExerciseData() = if (arrayIndex < exercises.size) {
        exercises[arrayIndex]
    } else {
        exercisesToRetry[0]
    }

    fun exercisesResultSentToServer() =
        saveUserDataState.value?.first == true

    private fun isBonusScreen() =
        exerciseIndex == getExercisesCount() && bonusExercises.isNotEmpty()

    fun checkExerciseResult() {

        if (checkExerciseResultJob == null || checkExerciseResultJob?.isCompleted == true) {
            answered.value = true
            exerciseResultSuccess = false
                if (exerciseUiModel is ExerciseUiModel.ExerciseUiModelExercise) {
                    checkExerciseResultJob = viewModelScope.launchExt(checkExerciseResultJob) {
                        when (val status =
                            exerciseResultInteractor.getExerciseResult(
                                exerciseData as ExerciseData.ExerciseDataExercise,
                                exerciseRequestData,
                                PrefsUtils.isOfflineModeEnabled()
                            )) {
                            is ExerciseResultStatus.NoConnection -> {
                                showBadConnectionDialogAlertFragmentState.value = true
                            }
                            is ExerciseResultStatus.ServiceUnavailable -> {
                                showBadConnectionDialogAlertFragmentState.value = true
                            }
                            is ExerciseResultStatus.Success -> {

                                exerciseResultSuccess = true

                                showBadConnectionDialogAlertFragmentState.value = false

                                snackbarState.value = exerciseUiModel to status.result
                                if (status.result.exerciseResult) {
                                    exerciseIndex++
                                    if (chapterPartIsFullyCompleted()){
                                        stopSecondsCounter()
                                        saveObtainedExercisesResult()
                                    }

                                    if (arrayIndex >= getSplitExercisesAndBonuses().first.size) {
                                        exercisesToRetry.removeAt(0)
                                    }

                                    progressState.value = getProgressPercent() to false
                                } else {

                                    if (arrayIndex >= getSplitExercisesAndBonuses().first.size)
                                        exercisesToRetry.removeAll(exercisesResponseData.exercises.filter {
                                            exercisesResponseData.exercises.indexOf(
                                                it
                                            ) >= exercisesResponseData.exercises.indexOf(
                                                exerciseData
                                            ) && it.exerciseNumber == exerciseData.exerciseNumber
                                        })
                                    else
                                        exercises.forEach {
                                            if (
                                                exercises.indexOf(it) > exercises.indexOf(
                                                    exerciseData
                                                ) && it.exerciseNumber == exerciseData.exerciseNumber
                                            ) {
                                                arrayIndex++
                                                exercises.remove(it)
                                            }
                                        }

                                    exercisesToRetry.addAll(exercisesResponseData.exercises.filter { it.exerciseNumber == exerciseData.exerciseNumber })
                                }

                                if (exerciseData.exerciseNumber == getSplitExercisesAndBonuses().first.last { it is ExerciseData.ExerciseDataExercise }.exerciseNumber && falseAnswers == 0)
                                    falseAnswers =
                                        exercisesToRetry.count { it is ExerciseData.ExerciseDataExercise }

                                if (((getSplitExercisesAndBonuses().first.indexOf(exerciseData) == getSplitExercisesAndBonuses().first.lastIndex) || (arrayIndex  >= getSplitExercisesAndBonuses().first.size)) && exercisesToRetry.isEmpty()) {
                                    exercisesAreCompleted = true
                                    if (isBonusCompleted)
                                        exercisesAreCompletedAndNoBonus = true
                                }

                                arrayIndex++
                            }
                        }
                    }
                } else {
                    if (arrayIndex >= getSplitExercisesAndBonuses().first.size) {
                        exercisesToRetry.removeAt(0)
                    }
                    if (((getSplitExercisesAndBonuses().first.indexOf(exerciseData) == getSplitExercisesAndBonuses().first.lastIndex) || (arrayIndex  >= getSplitExercisesAndBonuses().first.size)) && exercisesToRetry.isEmpty()) {
                        exercisesAreCompleted = true
                        if (isBonusCompleted)
                            exercisesAreCompletedAndNoBonus = true
                    }
                    arrayIndex++

                    initFragment()
                }
        } else if (showBadConnectionDialogAlertFragmentState.value == true) {
            checkExerciseResultJob?.cancel()
            showBadConnectionDialogAlertFragmentState.value = true
        }
    }

    fun checkBonusResult() {
        answered.value = true

        val trueAnswer =
            (exerciseData as ExerciseData.ExerciseDataExercise).exerciseAnswer?.split(",")
                ?.toSet() == exerciseRequestData.exerciseAnswer.split(",").toSet()
        if (trueAnswer)
            correctBonusAnswers++
        exerciseBonusResultState.value = trueAnswer

        navigateBonuses()
    }

    fun navigateBonuses(){
        viewModelScope.launch(Dispatchers.Main) {
            delay(app.resources.getInteger(R.integer.bonus_exercises_delay).toLong())
            exerciseBonusNavigationState.value = true
        }
    }

    private fun getFragmentToAdd(exerciseUiModel: ExerciseUiModel) =
        when (exerciseUiModel) {
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType1UiModel -> ExerciseVariantsTopTitleVariantsImagesFragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType2UiModel -> ExerciseVariantsTopLinearFragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType3UiModel,
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType4UiModel,
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType5UiModel,
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType24UiModel,
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType25UiModel  -> ExerciseVariantsTopTitleFragment()

            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType6UiModel -> ExerciseVariantsTopTitleHorizontalVariantsFragment()

            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType7UiModel -> ExerciseVariantsTrueFalseFragment()

            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType8UiModel,
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType12UiModel -> ExerciseVariantsTopTitleDoubleHorizontalVariantsFragment()

            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType9UiModel -> ExerciseInputEquationFragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType10UiModel -> ExerciseMissedNumberFragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType11UiModel -> ExerciseInputCollectionFragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType13UiModel -> ExerciseVariantsTopImagesEquationFragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType14UiModel -> ExerciseVariantsTopLinearFragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType15UiModel -> ExerciseVariantsTopTitleMultipleSelectFragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType16UiModel -> ExerciseMissedCharacterFragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType17UiModel -> ExerciseVariantsTopImagesRowVariantsImagesFragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType18UiModel -> ExerciseVariantsTopTitleImagesTrueFalseFragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType19UiModel -> ExerciseArrangeNumbersFragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType20UiModel -> ExerciseVariantsTitleNumberNumberNameTrueFalseFragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType21UiModel -> ExerciseVariantsTitleNumberNumberNameFragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType22UiModel -> ExerciseVariantsTrueFalseLongTitleFragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType23UiModel -> ExerciseVariantsMissedOperatorBlocksFragment()
            is ExerciseUiModel.ExerciseUiModelExercise.ExerciseType26UiModel -> ExerciseInputEquationResultFragment()

            is ExerciseUiModel.ExerciseUiModelScreen.ScreenType1UiModel -> ExerciseScreenType1()
            is ExerciseUiModel.ExerciseUiModelScreen.ScreenType2UiModel -> ExerciseScreenType2()
            is ExerciseUiModel.ExerciseUiModelScreen.ScreenType3UiModel -> ExerciseScreenType3()
            is ExerciseUiModel.ExerciseUiModelScreen.ScreenType4UiModel -> ExerciseScreenType4()
            is ExerciseUiModel.ExerciseUiModelScreen.ScreenType5UiModel -> ExerciseScreenType5()

            is ExerciseUiModel.ExplanationUiModel -> ExerciseExplanation1Fragment()
        }

    fun isInputExercise(): Boolean{
        return (exerciseData is ExerciseData.ExerciseDataExercise.ExerciseType9Data) ||
                (exerciseData is ExerciseData.ExerciseDataExercise.ExerciseType10Data) ||
                (exerciseData is ExerciseData.ExerciseDataExercise.ExerciseType11Data) ||
                (exerciseData is ExerciseData.ExerciseDataExercise.ExerciseType16Data) ||
                (exerciseData is ExerciseData.ExerciseDataExercise.ExerciseType26Data)
    }

    private fun getVariantsToSelectCount(): Int {
        return if(exerciseData is ExerciseData.ExerciseDataExercise.ExerciseType15Data) (exerciseData as ExerciseData.ExerciseDataExercise.ExerciseType15Data).correctCount else 1
    }

    fun getDescriptionFragment(): ExercisesDescriptionFragment {
        return when (chapterPartNumber) {
            1 -> ExercisesDescription1Fragment()
            2 -> ExercisesDescription2Fragment()
            4 -> ExercisesDescription4Fragment()
            5 -> ExercisesDescription5Fragment()
            7 -> ExercisesDescription7Fragment()
            9 -> ExercisesDescription9Fragment()
            10 -> ExercisesDescription10Fragment()
            12 -> ExercisesDescription12Fragment()
            13 -> ExercisesDescription13Fragment()
            else -> throw IOException("Unknown chapter part with description number")
        }
    }


    private fun getProgressPercent(): Float = exerciseIndex / getSplitExercisesAndBonuses().first.filterIsInstance<ExerciseData.ExerciseDataExercise>().size.toFloat()

    private fun getAnswersPercent(): Float =
        (getExercisesCount() - getFalseAnswers()) / getExercisesCount().toFloat()

    private fun getTrueAnswers(): Int = getExercisesCount() - getFalseAnswers()

    private fun getFalseAnswers(): Int = falseAnswers

    private fun getExercisesCount() = exercises.count { it is ExerciseData.ExerciseDataExercise }

    fun getExercisesEndFragment(oldUserDataData: UserDataData) =
        ExercisesEndFragment().apply {
            arguments = bundleOf(
                "TRUE_ANSWERS" to getTrueAnswers(),
                "FALSE_ANSWERS" to getFalseAnswers(),
                "ANSWERS_PERCENT" to getAnswersPercent(),
                "OBTAINED_XP" to obtainedXP,
                "PROCESS_SECONDS" to seconds,
                "OLD_USER_DATA" to Gson().toJson(oldUserDataData),
                "EXERCISES_BONUS_CORRECT_COUNT" to correctBonusAnswers
            )
        }

    fun getExercisesBonusEndFragment() =
        ExercisesBonusEndScreenFragment()
            .apply {
                arguments = bundleOf(
                    "OBTAINED_XP" to LevelUtils.getObtainedExercisesBonusXP(
                        correctBonusAnswers,
                        isTraining
                    ),
                    "CORRECT_ANSWERS_COUNT" to correctBonusAnswers
                )
            }

    fun endExercises() {
        if (progressState.value?.second == false) {
            progressState.value = 1F to true
        }
    }

    private fun getSplitExercisesAndBonuses(): Pair<List<ExerciseData>, List<ExerciseData>>{
       return exercisesResponseData.exercises.filter { !(it is ExerciseData.ExerciseDataExercise && it.isBonus) && !(it is ExerciseData.ExerciseDataScreen.ScreenType4Data && it.isBonusStart)} to
               exercisesResponseData.exercises.filter { (it is ExerciseData.ExerciseDataExercise && it.isBonus) || (it is ExerciseData.ExerciseDataScreen.ScreenType4Data && it.isBonusStart) }
    }

    fun startSecondsCounter() {
        secondsCounter = Timer()
        secondsCounter?.schedule(object : TimerTask() {
            override fun run() {
                seconds++
                if (bonusEndTime != 0L)
                    exercisesBonusRemainingTimeState.postValue(bonusEndTime - seconds)
                else if(exercisesResponseData.exercises.filterIsInstance(ExerciseData.ExerciseDataScreen.ScreenType4Data::class.java).any { it.isBonusStart })
                    exercisesBonusRemainingTimeState.postValue(exercisesResponseData.exercises.filterIsInstance(ExerciseData.ExerciseDataScreen.ScreenType4Data::class.java).first().bonusSeconds)
                Log.d("EXERCISES", "TIME IS $seconds SECONDS")
            }
        }, 1000, 1000)
    }

    fun stopSecondsCounter() {
        if (!secondsCounterIsStopped()) {
            secondsCounter!!.cancel()
            secondsCounter!!.purge()
            secondsCounter = null
            Log.d("EXERCISES", "TIME STOP")
        }
    }

    fun chapterPartIsFullyCompleted() = exerciseIndex == getExercisesCount() && isBonusCompleted

    fun secondsCounterIsStopped() = secondsCounter == null

    fun startExercisesBonus() {
        navigationState.value =
            ExercisesNavigationState.NAVIGATE_COMPLETELY to ExercisesBonusFragment()
    }

    enum class ExercisesNavigationState {
        FIRST,
        REPLACE,
        NAVIGATE_COMPLETELY,
        NAVIGATE_IN_CONTAINER
    }

    enum class ExercisesState {
        START_SCREEN,
        DESCRIPTION,
        EXERCISES,
        BONUS_SCREEN,
        EXPLANATION
    }
}