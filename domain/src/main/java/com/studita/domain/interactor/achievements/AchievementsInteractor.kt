package com.studita.domain.interactor.achievements

import com.studita.domain.entity.AchievementData
import com.studita.domain.interactor.GetAchievementsDataStatus
import com.studita.domain.interactor.GetAchievementsStatus
import kotlinx.coroutines.flow.Flow

interface AchievementsInteractor {
    fun getAchievements(userId: Int): Flow<GetAchievementsStatus>
    fun getAchievementsData(userId: Int? = null): Flow<GetAchievementsDataStatus>
}