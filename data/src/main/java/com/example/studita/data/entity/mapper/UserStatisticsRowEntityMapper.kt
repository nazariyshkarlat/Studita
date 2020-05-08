package com.example.studita.data.entity.mapper

import com.example.studita.data.entity.UserStatisticsRowEntity
import com.example.studita.domain.date.DateTimeFormat
import com.example.studita.domain.entity.UserStatisticsRowData

class UserStatisticsRowEntityMapper : Mapper<UserStatisticsRowData, UserStatisticsRowEntity>{

    override fun map(source: UserStatisticsRowData): UserStatisticsRowEntity = UserStatisticsRowEntity(DateTimeFormat().format(source.datetime), source.obtainedXP, source.obtainedTime, source.obtainedExercises, source.obtainedTrainings, source.obtainedAchievements)

}

class UserStatisticsRowDataMapper : Mapper<UserStatisticsRowEntity, UserStatisticsRowData>{

    override fun map(source: UserStatisticsRowEntity): UserStatisticsRowData = UserStatisticsRowData(DateTimeFormat().parse(source.datetime), source.obtainedXP, source.obtainedTime, source.obtainedExercises, source.obtainedTrainings, source.obtainedAchievements)

}