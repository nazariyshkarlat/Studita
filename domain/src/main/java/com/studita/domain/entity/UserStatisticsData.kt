package com.studita.domain.entity

import java.util.*

data class UserStatisticsData(
    val obtainedXP: Int,
    val timeSpent: Long,
    val completedExercises: Int,
    val completedTrainings: Int,
    val obtainedAchievements: Int,
    val maxDaysStreak: Long,
    val completedChapters: Int
)


data class UserStatisticsRowData(
    val datetime: Date,
    val obtainedXP: Int,
    val timeSpent: Long,
    val completedExercises: Int,
    val completedTrainings: Int,
    val daysStreak: Long,
    val completedChapters: Int
)

enum class UserStatisticsTime {
    TODAY,
    YESTERDAY,
    WEEK,
    MONTH
}

fun UserStatisticsTime.timeToString() = when (this) {
    UserStatisticsTime.TODAY -> "today"
    UserStatisticsTime.YESTERDAY -> "yesterday"
    UserStatisticsTime.WEEK -> "week"
    UserStatisticsTime.MONTH -> "month"
}