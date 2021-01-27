package com.studita.data.repository

import com.studita.data.entity.toBusinessEntity
import com.studita.data.repository.datasource.achievements.AchievementsDataStoreFactory
import com.studita.domain.entity.AchievementData
import com.studita.domain.entity.AchievementDataData
import com.studita.domain.repository.AchievementsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class AchievementsRepositoryImpl(private val achievementsDataStoreFactory: AchievementsDataStoreFactory) : AchievementsRepository {
    override fun getAchievements(userId: Int): Flow<List<AchievementData>> =
        achievementsDataStoreFactory.create().getAchievements(userId).map {list->
            list.map {
                it.toBusinessEntity()
            }
        }

    override fun getAchievementsData(userId: Int?): Flow<List<AchievementDataData>> =
        achievementsDataStoreFactory.create().getAchievementsData(userId).map {list->
            list.map {
                it.toBusinessEntity()
            }
        }

}