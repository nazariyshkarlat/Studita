package com.studita.presentation.model

import com.studita.domain.entity.AchievementData
import com.studita.domain.entity.AchievementDataData
import com.studita.domain.entity.AchievementLevel
import com.studita.domain.interactor.GetAchievementsDataStatus

sealed class AchievementsUiState{

    object NetworkError : AchievementsUiState()
    object ServerError : AchievementsUiState()
    object Loading : AchievementsUiState()
    object OfflineModeIsEnabled: AchievementsUiState()
    object AchievementsReceived : AchievementsUiState()

}

data class AchievementUiModel(val iconUrl: String, val isImprovable: Boolean)

fun GetAchievementsDataStatus.toAchievementsUiState() = when(this){
    is GetAchievementsDataStatus.Success -> AchievementsUiState.AchievementsReceived
    is GetAchievementsDataStatus.ServiceUnavailable, GetAchievementsDataStatus.Failure -> AchievementsUiState.ServerError
    is GetAchievementsDataStatus.NoConnection -> AchievementsUiState.NetworkError
}

fun List<AchievementData>.toUiModels() = this.sortedWith(compareBy ({ AchievementLevel.values().size - it.level.ordinal }, { !it.isImprovable})).map { AchievementUiModel(it.iconUrl, it.isImprovable) }