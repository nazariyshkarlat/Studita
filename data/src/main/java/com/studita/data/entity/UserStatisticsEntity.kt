package com.studita.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.studita.domain.date.DateTimeFormat
import com.studita.domain.entity.UserStatisticsData
import com.studita.domain.entity.UserStatisticsRowData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class UserStatisticsEntity(
    @SerialName("time_type") val timeType: String,
    @SerialName("obtained_XP") val obtainedXP: Int,
    @SerialName("time_spent") val timeSpent: Long,
    @SerialName("completed_exercises") val completedExercises: Int,
    @SerialName("completed_trainings") val completedTrainings: Int,
    @SerialName("obtained_achievements") val obtainedAchievements: Int,
    @SerialName("max_streak_days") val maxDaysStreak: Long,
    @SerialName("completed_chapters") val completedChapters: Int
)

@Serializable
@Entity(tableName = UserStatisticsRowEntity.TABLE_NAME)
data class UserStatisticsRowEntity(
    @SerialName("datetime") val datetime: String,
    @SerialName("obtained_XP") val obtainedXP: Int,
    @SerialName("time_spent") val obtainedTime: Long,
    @SerialName("completed_exercises") val completedExercises: Int,
    @SerialName("completed_trainings") val completedTrainings: Int,
    @SerialName("days_streak") val daysStreak: Long,
    @SerialName("completed_chapters") val completedChapters: Int
) {
    companion object {
        const val TABLE_NAME = "user_statistics"
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "record_id")
    @Transient
    var recordId: Int = 0
}


fun UserStatisticsEntity.toBusinessEntity() = UserStatisticsData(
    obtainedXP,
    timeSpent,
    completedExercises,
    completedTrainings,
    obtainedAchievements,
    maxDaysStreak,
    completedChapters
)

fun UserStatisticsRowEntity.toBusinessEntity() = UserStatisticsRowData(
    DateTimeFormat().parse(datetime)!!,
    obtainedXP,
    obtainedTime,
    completedExercises,
    completedTrainings,
    daysStreak,
    completedChapters
)

fun UserStatisticsRowData.toRawEntity() = UserStatisticsRowEntity(
    DateTimeFormat().format(datetime),
    obtainedXP,
    timeSpent,
    completedExercises,
    completedTrainings,
    daysStreak,
    completedChapters
)
