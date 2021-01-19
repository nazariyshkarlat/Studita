package com.studita.presentation.view_model

import android.app.Application
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studita.R
import com.studita.domain.entity.CompleteExercisesRequestData
import com.studita.domain.entity.CompletedExercisesData
import com.studita.domain.entity.UserDataData
import com.studita.domain.entity.UserStatisticsRowData
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
import com.studita.domain.entity.exercise.*
import com.studita.domain.interactor.complete_chapter_part.CompleteExercisesInteractor
import com.studita.domain.interactor.exercises.ExerciseResultInteractor
import com.studita.domain.interactor.exercises.ExercisesInteractor
import com.studita.domain.interactor.user_data.UserDataInteractor
import com.studita.domain.interactor.user_statistics.UserStatisticsInteractor
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.context.GlobalContext
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ExercisesViewModel(val app: Application, val chapterPartNumber: Int, private val handle: SavedStateHandle) : ViewModel() {

    companion object{
        const val EXERCISE_DATA = "EXERCISE_DATA"
        const val EXERCISES_RESPONSE_DATA = "EXERCISES_RESPONSE_DATA"
        const val EXERCISES = "EXERCISES"
        const val  EXERCISE_REQUEST_DATA = "EXERCISE_REQUEST_DATA"
        const val BONUS_EXERCISES = "BONUS_EXERCISES"
        const val EXERCISES_TO_RETRY = "EXERCISES_TO_RETRY"
        const val IS_BONUS_COMPLETED = "IS_BONUS_COMPLETED"
        const val IS_TRAINING = "IS_TRAINING"
        const val EXERCISES_ARE_COMPLETED_AND_NO_BONUS = "EXERCISES_ARE_COMPLETED_AND_NO_BONUS"
        const val EXERCISES_ARE_COMPLETED = "EXERCISES_ARE_COMPLETED"
        const val EXERCISE_RESULT_SUCCESS = "EXERCISE_RESULT_SUCCESS"
        const val TIME_COUNTER_IS_PAUSED = "TIME_COUNTER_IS_PAUSED"
        const val FEEDBACK_WAS_SENT = "FEEDBACK_WAS_SENT"
        const val CHAPTER_PARTS_IN_CHAPTER_COUNT = "CHAPTER_PARTS_IN_CHAPTER_COUNT"
        const val CHAPTER_PART_IN_CHAPTER_NUMBER = "CHAPTER_PART_IN_CHAPTER_NUMBER"
        const val CHAPTER_NUMBER = "CHAPTER_NUMBER"
        const val EXERCISE_COUNT_TO_SELECT = "EXERCISE_COUNT_TO_SELECT"
        const val EXERCISES_IN_CHAPTER_COUNT = "EXERCISES_IN_CHAPTER_COUNT"
        const val ARRAY_INDEX = "ARRAY_INDEX"
        const val BONUS_INDEX = "BONUS_INDEX"
        const val EXERCISE_INDEX = "EXERCISE_INDEX"
        const val OBTAINED_XP = "OBTAINED_XP"
        const val CHAPTER_NAME = "CHAPTER_NAME"
        const val FALSE_ANSWERS = "FALSE_ANSWERS"
        const val CORRECT_BONUS_ANSWERS = "CORRECT_BONUS_ANSWERS"
        const val SECONDS = "SECONDS"
        const val BONUS_END_TIME = "BONUS_END_TIME"

        const val SAVE_USER_DATA_STATE = "SAVE_USER_DATA_STATE"
        const val SHOW_BAD_CONNECTION_DIALOG_ALERT_FRAGMENT_STATE = "SHOW_BAD_CONNECTION_DIALOG_ALERT_FRAGMENT_STATE"
        const val TOOLBAR_PROGRESS_BAR_ANIM_STATE = "TOOLBAR_PROGRESS_BAR_ANIM_STATE"
        const val PROGRESS_STATE = "PROGRESS_STATE"
        const val SNACKBAR_STATE = "SNACKBAR_STATE"
        const val PROGRESS_BAR_VISIBLE_STATE = "PROGRESS_BAR_VISIBLE_STATE"
        const val ANSWERED = "ANSWERED"
        const val BUTTON_ENABLED_STATE = "BUTTON_ENABLED_STATE"
        const val BUTTON_TEXT_STATE = "BUTTON_TEXT_STATE"
        const val EXERCISES_PROGRESS = "EXERCISES_PROGRESS"
        const val EXERCISES_BONUS_REMAINING_TIME_STATE = "EXERCISES_BONUS_REMAINING_TIME_STATE"
        const val TRANSPARENT_LAYOUTS_ARE_VISIBLE_STATE= "TRANSPARENT_LAYOUTS_ARE_VISIBLE_STATE"
        const val EXERCISE_BONUS_RESULT_STATE = "EXERCISE_BONUS_RESULT_STATE"

        const val CONNECTION_TIMEOUT = 3000L
    }

    val progressBarVisibleState = MutableLiveData<Boolean>(false)
    val answered = MutableLiveData<Boolean>(false)
    val buttonEnabledState = MutableLiveData<Boolean>()
    val buttonTextState = MutableLiveData<String>()
    val exercisesProgress = MutableLiveData(ExercisesState.START_SCREEN)
    val exercisesBonusRemainingTimeState = MutableLiveData<Long>()
    val transparentLayoutsAreVisibleState = MutableLiveData<Boolean>()
    val exerciseBonusResultState = MutableLiveData<Boolean>()
    val loadScreenBadConnectionState = MutableLiveData<Boolean>()

    val exercisesEvent = SingleLiveEvent<Boolean>()
    val endButtonEvent = SingleLiveEvent<Boolean>()
    val navigationEvent = SingleLiveEvent<Pair<ExercisesNavigationState, Fragment>>()
    val toolbarDividerEvent = SingleLiveEvent<Boolean>()
    val buttonDividerEvent = SingleLiveEvent<Boolean>()
    val selectVariantEvent = SingleLiveEvent<Int>()
    val exerciseBonusNavigationEvent = SingleLiveEvent<Boolean>()

    val progressEvent = SingleLiveEvent<Pair<Float, Boolean>>()
    fun getProgressState(): Pair<Float, Boolean>? =
        if(progressEvent.value != null) progressEvent.value
        else handle.get<String>(PROGRESS_STATE)?.let{
            Json.decodeFromString(it)
        }

    val snackbarEvent = SingleLiveEvent<Pair<ExerciseUiModel, ExerciseResponseData>?>()
    fun getSnackbarState(): Pair<ExerciseUiModel, ExerciseResponseData>? =
        if(snackbarEvent.value != null) snackbarEvent.value
        else handle.get<String>(SNACKBAR_STATE)?.let {
            with(Json.decodeFromString<Pair<ExerciseData, ExerciseResponseData>>(it)){
                this.first.toUiModel(app) to this.second
            }
        }

    val toolbarProgressBarAnimEvent = SingleLiveEvent<Boolean>()
    fun getToolbarProgressBarAnimEvent(): Boolean? =
        if(toolbarProgressBarAnimEvent.value != null) toolbarProgressBarAnimEvent.value
        else handle.get<Boolean>(TOOLBAR_PROGRESS_BAR_ANIM_STATE)

    val showBadConnectionDialogAlertFragmentEvent = SingleLiveEvent<Boolean>()
    fun getShowBadConnectionDialogAlertFragmentState(): Boolean? =
        if(showBadConnectionDialogAlertFragmentEvent.value != null) showBadConnectionDialogAlertFragmentEvent.value
        else handle.get<Boolean>(SHOW_BAD_CONNECTION_DIALOG_ALERT_FRAGMENT_STATE)

    val saveUserDataEvent = SingleLiveEvent<Pair<Boolean, UserDataData>>()
    fun getSaveUserDataState(): Pair<Boolean, UserDataData>? =
        if(saveUserDataEvent.value != null) saveUserDataEvent.value
        else handle.get<String>(SAVE_USER_DATA_STATE)?.let{
            Json.decodeFromString(it)
        }

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
    private var falseAnswers = 0
    var correctBonusAnswers = 0
    private var seconds = 0L
    var bonusEndTime = 0L
    var chapterName: String? = null

    var exerciseRequestData: ExerciseRequestData? = null
    var exerciseData: ExerciseData? = null
    var exerciseUiModel: ExerciseUiModel? = null
    var exercisesResponseData: ExercisesResponseData? = null
    var exercises: ArrayList<ExerciseData> = ArrayList<ExerciseData>()
    private var bonusExercises = emptyList<ExerciseData>()
    private val exercisesToRetry = ArrayList<ExerciseData>()

    private val userStatisticsInteractor: UserStatisticsInteractor by GlobalContext.get().inject()
    private val userDataInteractor = GlobalContext.get().get<UserDataInteractor>()
    private val completeExercisesInteractor = GlobalContext.get().get<CompleteExercisesInteractor>()
    private val exercisesInteractor = GlobalContext.get().get<ExercisesInteractor>()
    private val exerciseResultInteractor = GlobalContext.get().get<ExerciseResultInteractor>()

    private var secondsCounter: Timer? = null

    var saveObtainedExercisesDataJob: Job? = null
    var badConnectionJob: Job? = null
    private var getExercisesJob: Job? = null
    private var checkExerciseResultJob: Job? = null

    init {
        restoreState()

        if(exercisesResponseData == null)
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
                    exercisesEvent.value = true
                    exercisesResponseData = status.result

                    if (exercisesResponseData!!.exercises.any { it is ExerciseData.ExerciseDataScreen.ScreenType4Data && it.isBonusStart }) {
                        isBonusCompleted = false
                        val splitExercises = getSplitExercisesAndBonuses()
                        exercises = ArrayList(splitExercises.first)
                        bonusExercises = ArrayList(splitExercises.second)
                    } else
                        exercises = ArrayList(exercisesResponseData!!.exercises)

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
            delay(CONNECTION_TIMEOUT)

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
                    saveUserDataEvent.value = true to oldUserDataData
            }else -> {
            if(!PrefsUtils.isOfflineModeEnabled() && getShowBadConnectionDialogAlertFragmentState() != true)
                showBadConnectionDialogAlertFragmentEvent.value = true
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
                        saveUserDataEvent.value = true to userDataStatus.result
                    }

                    completeExercises(
                        data,
                        userDataStatus.result
                    )
                }
            }else{
                showBadConnectionDialogAlertFragmentEvent.value = true
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
        endButtonEvent.value = show
    }

    fun setButtonText(text: String) {
        buttonTextState.value = text
    }

    fun setButtonEnabled(enabled: Boolean) {
        buttonEnabledState.value = enabled
    }

    fun showToolbarDivider(show: Boolean) {
        toolbarDividerEvent.value = show
    }

    fun showButtonDivider(show: Boolean) {
        buttonDividerEvent.value = show
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
        snackbarEvent.value = null
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

            exerciseUiModel = exerciseData!!.toUiModel(app)
            val exerciseFragment = getFragmentToAdd(exerciseUiModel!!)
            viewModelScope.launch(Dispatchers.Main) {
                navigationEvent.value = when {
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
        exerciseUiModel = exerciseData!!.toUiModel(app)
        bonusIndex++
        return getFragmentToAdd(exerciseUiModel!!)
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
        getSaveUserDataState()?.first == true

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
                                exerciseRequestData!!,
                                PrefsUtils.isOfflineModeEnabled()
                            )) {
                            is ExerciseResultStatus.NoConnection -> {
                                showBadConnectionDialogAlertFragmentEvent.value = true
                            }
                            is ExerciseResultStatus.ServiceUnavailable -> {
                                showBadConnectionDialogAlertFragmentEvent.value = true
                            }
                            is ExerciseResultStatus.Success -> {

                                exerciseResultSuccess = true

                                showBadConnectionDialogAlertFragmentEvent.value = false

                                snackbarEvent.value = exerciseUiModel!! to status.result
                                if (status.result.exerciseResult) {
                                    exerciseIndex++
                                    if (chapterPartIsFullyCompleted()){
                                        stopSecondsCounter()
                                        saveObtainedExercisesResult()
                                    }

                                    if (arrayIndex >= getSplitExercisesAndBonuses().first.size) {
                                        exercisesToRetry.removeAt(0)
                                    }

                                    progressEvent.value = getProgressPercent() to false
                                } else {

                                    if (arrayIndex >= getSplitExercisesAndBonuses().first.size)
                                        exercisesToRetry.removeAll(exercisesResponseData!!.exercises.filter {
                                            exercisesResponseData!!.exercises.indexOf(
                                                it
                                            ) >= exercisesResponseData!!.exercises.indexOf(
                                                exerciseData
                                            ) && it.exerciseNumber == exerciseData!!.exerciseNumber
                                        })
                                    else
                                        exercises.forEach {
                                            if (
                                                exercises.indexOf(it) > exercises.indexOf(
                                                    exerciseData
                                                ) && it.exerciseNumber == exerciseData!!.exerciseNumber
                                            ) {
                                                arrayIndex++
                                                exercises.remove(it)
                                            }
                                        }

                                    exercisesToRetry.addAll(exercisesResponseData!!.exercises.filter { it.exerciseNumber == exerciseData!!.exerciseNumber })
                                }

                                if (exerciseData!!.exerciseNumber == getSplitExercisesAndBonuses().first.last { it is ExerciseData.ExerciseDataExercise }.exerciseNumber && falseAnswers == 0)
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
        } else if (getShowBadConnectionDialogAlertFragmentState() == true) {
            checkExerciseResultJob?.cancel()
            showBadConnectionDialogAlertFragmentEvent.value = true
        }
    }

    fun checkBonusResult() {
        answered.value = true

        val trueAnswer =
            (exerciseData as ExerciseData.ExerciseDataExercise).exerciseAnswer?.split(",")
                ?.toSet() == exerciseRequestData!!.exerciseAnswer.split(",").toSet()
        if (trueAnswer)
            correctBonusAnswers++
        exerciseBonusResultState.value = trueAnswer

        navigateBonuses()
    }

    fun navigateBonuses(){
        viewModelScope.launch(Dispatchers.Main) {
            delay(app.resources.getInteger(R.integer.bonus_exercises_delay).toLong())
            exerciseBonusNavigationEvent.value = true
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
        if (getProgressState()?.second == false) {
            progressEvent.value = 1F to true
        }
    }

    private fun getSplitExercisesAndBonuses(): Pair<List<ExerciseData>, List<ExerciseData>>{
       return exercisesResponseData!!.exercises.filter { !(it is ExerciseData.ExerciseDataExercise && it.isBonus) && !(it is ExerciseData.ExerciseDataScreen.ScreenType4Data && it.isBonusStart)} to
               exercisesResponseData!!.exercises.filter { (it is ExerciseData.ExerciseDataExercise && it.isBonus) || (it is ExerciseData.ExerciseDataScreen.ScreenType4Data && it.isBonusStart) }
    }

    fun startSecondsCounter() {
        secondsCounter = Timer()
        secondsCounter?.schedule(object : TimerTask() {
            override fun run() {
                seconds++
                if (bonusEndTime != 0L)
                    exercisesBonusRemainingTimeState.postValue(bonusEndTime - seconds)
                else if(exercisesResponseData!!.exercises.filterIsInstance(ExerciseData.ExerciseDataScreen.ScreenType4Data::class.java).any { it.isBonusStart })
                    exercisesBonusRemainingTimeState.postValue(exercisesResponseData!!.exercises.filterIsInstance(ExerciseData.ExerciseDataScreen.ScreenType4Data::class.java).first().bonusSeconds)
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
        navigationEvent.value =
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


    fun saveState(){
        if(exerciseData != null)
            handle.set(EXERCISE_DATA, Json.encodeToString(exerciseData))
        if(exercisesResponseData != null)
            handle.set(EXERCISES_RESPONSE_DATA, Json.encodeToString(exercisesResponseData))
        if(exerciseRequestData != null)
            handle.set(EXERCISE_REQUEST_DATA, Json.encodeToString(exerciseRequestData))
        if(chapterName != null)
            handle.set(CHAPTER_NAME, chapterName)
        handle.set(EXERCISES, Json.encodeToString(exercises))
        handle.set(BONUS_EXERCISES, Json.encodeToString(bonusExercises))
        handle.set(EXERCISES_TO_RETRY, Json.encodeToString(exercisesToRetry))
        handle.set(IS_BONUS_COMPLETED, isBonusCompleted)
        handle.set(IS_TRAINING, isTraining)
        handle.set(EXERCISES_ARE_COMPLETED, exercisesAreCompleted)
        handle.set(EXERCISES_ARE_COMPLETED_AND_NO_BONUS, exercisesAreCompletedAndNoBonus)
        handle.set(EXERCISE_RESULT_SUCCESS, exerciseResultSuccess)
        handle.set(TIME_COUNTER_IS_PAUSED, timeCounterIsPaused)
        handle.set(FEEDBACK_WAS_SENT, feedbackWasSent)
        handle.set(CHAPTER_PART_IN_CHAPTER_NUMBER, chapterPartInChapterNumber)
        handle.set(CHAPTER_PARTS_IN_CHAPTER_COUNT, chapterPartsInChapterCount)
        handle.set(CHAPTER_NUMBER, chapterNumber)
        handle.set(EXERCISES_IN_CHAPTER_COUNT, exercisesInChapterCount)
        handle.set(ARRAY_INDEX, arrayIndex)
        handle.set(BONUS_INDEX, bonusIndex)
        handle.set(EXERCISE_INDEX, exerciseIndex)
        handle.set(OBTAINED_XP, obtainedXP)
        handle.set(EXERCISE_COUNT_TO_SELECT, exerciseCountToSelect)
        handle.set(BONUS_END_TIME, bonusEndTime)
        handle.set(SECONDS, seconds)
        handle.set(CORRECT_BONUS_ANSWERS, correctBonusAnswers)
        handle.set(FALSE_ANSWERS, falseAnswers)
        handle.set(PROGRESS_BAR_VISIBLE_STATE, progressBarVisibleState.value)
        handle.set(ANSWERED, answered.value)
        handle.set(BUTTON_ENABLED_STATE, buttonEnabledState.value)
        handle.set(BUTTON_TEXT_STATE, buttonTextState.value)
        handle.set(EXERCISES_PROGRESS, exercisesProgress.value)
        handle.set(EXERCISES_BONUS_REMAINING_TIME_STATE, exercisesBonusRemainingTimeState.value)
        handle.set(TRANSPARENT_LAYOUTS_ARE_VISIBLE_STATE, transparentLayoutsAreVisibleState.value)
        handle.set(EXERCISE_BONUS_RESULT_STATE, exerciseBonusResultState.value)

        if(saveUserDataEvent.value != null)
            handle.set(SAVE_USER_DATA_STATE, Json.encodeToString(saveUserDataEvent.value))
        if(showBadConnectionDialogAlertFragmentEvent.value != null)
            handle.set(SHOW_BAD_CONNECTION_DIALOG_ALERT_FRAGMENT_STATE, showBadConnectionDialogAlertFragmentEvent.value)
        if(toolbarProgressBarAnimEvent.value != null)
            handle.set(TOOLBAR_PROGRESS_BAR_ANIM_STATE, toolbarProgressBarAnimEvent.value)
        if(progressEvent.value != null)
            handle.set(PROGRESS_STATE, Json.encodeToString(progressEvent.value))
        if(snackbarEvent.value != null)
            handle.set(SNACKBAR_STATE, Json.encodeToString(exerciseData!! to snackbarEvent.value!!.second))
    }

    private fun restoreState(){
        handle.get<String>(EXERCISE_DATA)?.let{
            exerciseData = Json.decodeFromString(it)
            exerciseUiModel = exerciseData!!.toUiModel(app)
        }
        handle.get<String>(EXERCISES_RESPONSE_DATA)?.let{
            exercisesResponseData = Json.decodeFromString(it)
        }
        handle.get<String>(EXERCISE_REQUEST_DATA)?.let{
            exerciseRequestData = Json.decodeFromString(it)
        }
        handle.get<String>(CHAPTER_NAME)?.let{
            chapterName = it
        }
        handle.get<String>(EXERCISES)?.let{
            exercises = Json.decodeFromString(it)
        }
        handle.get<String>(BONUS_EXERCISES)?.let{
            bonusExercises = Json.decodeFromString(it)
        }
        handle.get<String>(EXERCISES_TO_RETRY)?.let{
            exercisesToRetry.clear()
            exercisesToRetry.addAll(Json.decodeFromString<List<ExerciseData>>(it))
        }
        handle.get<Boolean>(IS_BONUS_COMPLETED)?.let{
            isBonusCompleted = it
        }
        handle.get<Boolean>(IS_TRAINING)?.let{
            isTraining = it
        }
        handle.get<Boolean>(EXERCISES_ARE_COMPLETED)?.let{
            exercisesAreCompleted = it
        }
        handle.get<Boolean>(EXERCISES_ARE_COMPLETED_AND_NO_BONUS)?.let{
            exercisesAreCompletedAndNoBonus = it
        }
        handle.get<Boolean>(EXERCISE_RESULT_SUCCESS)?.let{
            exerciseResultSuccess = it
        }
        handle.get<Boolean>(TIME_COUNTER_IS_PAUSED)?.let{
            timeCounterIsPaused = it
        }
        handle.get<Boolean>(FEEDBACK_WAS_SENT)?.let{
            feedbackWasSent = it
        }
        handle.get<Int>(CHAPTER_PART_IN_CHAPTER_NUMBER)?.let{
            chapterPartInChapterNumber = it
        }
        handle.get<Int>(CHAPTER_PARTS_IN_CHAPTER_COUNT)?.let{
            chapterPartsInChapterCount = it
        }
        handle.get<Int>(CHAPTER_NUMBER)?.let{
            chapterNumber = it
        }
        handle.get<Int>(EXERCISES_IN_CHAPTER_COUNT)?.let{
            exercisesInChapterCount = it
        }
        handle.get<Int>(ARRAY_INDEX)?.let{
            arrayIndex = it
        }
        handle.get<Int>(BONUS_INDEX)?.let{
            bonusIndex = it
        }
        handle.get<Int>(EXERCISE_INDEX)?.let{
            exerciseIndex = it
        }
        handle.get<Int>(OBTAINED_XP)?.let{
            obtainedXP = it
        }
        handle.get<Int>(EXERCISE_COUNT_TO_SELECT)?.let{
            exerciseCountToSelect = it
        }
        handle.get<Long>(BONUS_END_TIME)?.let{
            bonusEndTime = it
        }
        handle.get<Long>(SECONDS)?.let{
            seconds = it
        }
        handle.get<Int>(CORRECT_BONUS_ANSWERS)?.let{
            correctBonusAnswers = it
        }
        handle.get<Int>(FALSE_ANSWERS)?.let{
            falseAnswers = it
        }
        handle.get<Boolean>(PROGRESS_BAR_VISIBLE_STATE)?.let{
            progressBarVisibleState.value = it
        }
        handle.get<Boolean>(PROGRESS_BAR_VISIBLE_STATE)?.let{
            progressBarVisibleState.value = it
        }
        handle.get<Boolean>(ANSWERED)?.let{
            answered.value = it
        }
        handle.get<Boolean>(BUTTON_ENABLED_STATE)?.let{
            buttonEnabledState.value = it
        }
        handle.get<String>(BUTTON_TEXT_STATE)?.let{
            buttonTextState.value = it
        }
        handle.get<ExercisesState>(EXERCISES_PROGRESS)?.let{
            exercisesProgress.value = it
        }
        handle.get<Long>(EXERCISES_BONUS_REMAINING_TIME_STATE)?.let{
            exercisesBonusRemainingTimeState.value = it
        }
        handle.get<Boolean>(TRANSPARENT_LAYOUTS_ARE_VISIBLE_STATE)?.let{
            transparentLayoutsAreVisibleState.value = it
        }
        handle.get<Boolean>(EXERCISE_BONUS_RESULT_STATE)?.let{
            exerciseBonusResultState.value = it
        }
    }
}