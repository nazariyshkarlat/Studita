package com.studita.data.repository.datasource.achievements

import com.studita.data.entity.AchievementDataEntity
import com.studita.data.entity.AchievementEntity
import kotlinx.coroutines.flow.Flow

interface AchievementsDataStore {

    fun getAchievements(userId: Int): Flow<List<AchievementEntity>>

    fun getAchievementsData(userId: Int?): Flow<List<AchievementDataEntity>>

}