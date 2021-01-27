package com.studita.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studita.domain.entity.AchievementDataData
import com.studita.domain.entity.AchievementType
import com.studita.domain.interactor.GetAchievementsDataStatus
import com.studita.domain.interactor.achievements.AchievementsInteractor
import com.studita.presentation.model.AchievementsUiState
import com.studita.presentation.model.toAchievementsUiState
import com.studita.utils.PrefsUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class AchievementsViewModel(private val userId: Int?, private val achievementsInteractor: AchievementsInteractor) : ViewModel() {

    private val _uiState: MutableStateFlow<AchievementsUiState> =MutableStateFlow(
        if(!PrefsUtils.isOfflineModeEnabled())
            AchievementsUiState.Loading
        else
            AchievementsUiState.OfflineModeIsEnabled
    )
    val uiState: StateFlow<AchievementsUiState> = _uiState

    private val _recyclerDividerIsVisibleState: MutableStateFlow<Boolean> =MutableStateFlow(false)
    val recyclerDividerIsVisibleState: StateFlow<Boolean> = _recyclerDividerIsVisibleState

    private val _scrollRecyclersToTopEvent = BroadcastChannel<Unit>(Channel.BUFFERED)
    val scrollRecyclersToTopEvent = _scrollRecyclersToTopEvent.asFlow()

    private val _uiStateUpdateEvent = BroadcastChannel<Unit>(Channel.BUFFERED)
    val uiStateUpdateEvent = _uiStateUpdateEvent.asFlow()

    private val _viewPagerNavigateToPageEvent = BroadcastChannel<Int>(Channel.BUFFERED)
    val viewPagerNavigateToPageEvent = _viewPagerNavigateToPageEvent.asFlow()

    private val _recyclerViewScrollToItemEvent = BroadcastChannel<Pair<AchievementsCategory, Int>>(Channel.BUFFERED)
    val recyclerViewScrollToItemEvent = _recyclerViewScrollToItemEvent.asFlow()
    enum class AchievementsCategory{
        IMPROVABLE, NON_IMPROVABLE
    }

    private val _improvableAchievementsState: MutableStateFlow<List<AchievementDataData.ImprovableAchievementData>> =
        MutableStateFlow(emptyList())
    val improvableAchievementsState: StateFlow<List<AchievementDataData.ImprovableAchievementData>> =
        _improvableAchievementsState

    private val _nonImprovableAchievementsState: MutableStateFlow<List<AchievementDataData.NonImprovableAchievementData>> =
        MutableStateFlow(emptyList())
    val nonImprovableAchievementsState: StateFlow<List<AchievementDataData.NonImprovableAchievementData>> =
        _nonImprovableAchievementsState
    private var getAchievementsJob: Job? = null

    init {
        if(_uiState.value == AchievementsUiState.Loading)
            getAchievements()
    }

    fun updateDividerState(scrollY: Int){
        viewModelScope.launch {
            _recyclerDividerIsVisibleState.value = scrollY > 0
        }
    }

    fun onEnableOfflineMode(){
        getAchievementsJob?.cancel()
        viewModelScope.launch {
            _scrollRecyclersToTopEvent.send(Unit)
        }
        _uiState.value = AchievementsUiState.OfflineModeIsEnabled
    }

    fun navigateToAchievement(achievementType: AchievementType){
        (uiState.value as?AchievementsUiState.AchievementsReceived)?.let {
            viewModelScope.launch {
                delay(200L)
                improvableAchievementsState.value.indexOfFirst { it.type == achievementType  }.let{
                    if(it != -1) {
                        _viewPagerNavigateToPageEvent.send(1)
                        delay(200L)
                        _recyclerViewScrollToItemEvent.send(AchievementsCategory.IMPROVABLE to it)
                    }
                }
                nonImprovableAchievementsState.value.indexOfFirst { it.type == achievementType  }.let{
                    if(it != -1) {
                        _viewPagerNavigateToPageEvent.send(0)
                        delay(200L)
                        _recyclerViewScrollToItemEvent.send(AchievementsCategory.NON_IMPROVABLE to it)
                    }
                }
            }
        }
    }

    fun getAchievements(isPageRefresh: Boolean = false){
        if(!isPageRefresh)
            _uiState.value = AchievementsUiState.Loading
        getAchievementsJob = viewModelScope.launch {
            achievementsInteractor.getAchievementsData(userId).collect {
                if(it is GetAchievementsDataStatus.Success) {
                    _improvableAchievementsState.value = it.achievements.filterIsInstance(AchievementDataData.ImprovableAchievementData::class.java)
                    _nonImprovableAchievementsState.value = it.achievements.filterIsInstance(AchievementDataData.NonImprovableAchievementData::class.java)
                }

                _uiState.value = it.toAchievementsUiState()
                _uiStateUpdateEvent.send(Unit)
            }
        }
    }

}