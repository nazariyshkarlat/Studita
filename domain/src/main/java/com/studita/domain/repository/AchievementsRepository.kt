package com.studita.domain.repository

import com.studita.domain.entity.AchievementData
import com.studita.domain.entity.AchievementDataData
import kotlinx.coroutines.flow.Flow

interface AchievementsRepository {

    fun getAchievements(userId: Int): Flow<List<AchievementData>>

    fun getAchievementsData(userId: Int?): Flow<List<AchievementDataData>>

}