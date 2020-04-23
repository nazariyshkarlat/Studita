package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.UserStatisticsEntity
import com.example.studita.domain.entity.UserStatisticsData

class UserStatisticsDataMapper : Mapper<List<UserStatisticsEntity>, List<UserStatisticsData>>{
    override fun map(source: List<UserStatisticsEntity>) = source.map { mapEntity(it) }

    private fun mapEntity(source: UserStatisticsEntity) = UserStatisticsData(source.obtainedXP, source.obtainedTime, source.obtainedExercises, source.obtainedTrainings, source.obtainedAchievements)
}