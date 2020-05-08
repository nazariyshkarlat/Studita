package com.example.studita.domain.entity

import java.util.*

data class UserStatisticsData(val obtainedXP: Int, val obtainedTime: Long, val obtainedExercises: Int, val obtainedTrainings: Int, val obtainedAchievements: Int)


data class UserStatisticsRowData(val datetime: Date,
                                 val obtainedXP: Int?,
                                 val obtainedTime: Long?,
                                 val obtainedExercises: Int?,
                                 val obtainedTrainings: Int?,
                                 val obtainedAchievements: Int?)