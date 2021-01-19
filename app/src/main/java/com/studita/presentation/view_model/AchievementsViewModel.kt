package com.studita.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.studita.domain.entity.AchievementDataData
import com.studita.domain.interactor.GetAchievementsDataStatus
import com.studita.domain.interactor.achievements.AchievementsInteractor
import com.studita.presentation.model.AchievementsUiState
import com.studita.presentation.model.toAchievementsUiState
import com.studita.utils.PrefsUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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

    fun getAchievements(){
        _uiState.value = AchievementsUiState.Loading
        getAchievementsJob = viewModelScope.launch {
            println("get achievemnts data")
            achievementsInteractor.getAchievementsData(userId).collect {
                println("collect achievemnts data")
                _uiState.value = it.toAchievementsUiState()

                if(it is GetAchievementsDataStatus.Success) {
                    _improvableAchievementsState.value = it.achievements.filterIsInstance(AchievementDataData.ImprovableAchievementData::class.java)
                    _nonImprovableAchievementsState.value = it.achievements.filterIsInstance(AchievementDataData.NonImprovableAchievementData::class.java)
                }
            }
        }
    }

}