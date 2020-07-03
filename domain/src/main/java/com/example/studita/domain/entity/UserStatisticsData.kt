package com.example.studita.domain.entity

import java.util.*

data class UserStatisticsData(val obtainedXP: Int, val obtainedTime: Long, val obtainedExercises: Int, val obtainedTrainings: Int, val obtainedAchievements: Int)


data class UserStatisticsRowData(val datetime: Date,
                                 val obtainedXP: Int?,
                                 val obtainedTime: Long?,
                                 val obtainedExercises: Int?,
                                 val obtainedTrainings: Int?,
                                 val obtainedAchievements: Int?)

enum class UserStatisticsTime{
    TODAY,
    YESTERDAY,
    WEEK,
    MONTH
}

fun UserStatisticsTime.timeToString() =  when(this){
    UserStatisticsTime.TODAY -> "today"
    UserStatisticsTime.YESTERDAY -> "yesterday"
    UserStatisticsTime.WEEK -> "week"
    UserStatisticsTime.MONTH -> "month"
}