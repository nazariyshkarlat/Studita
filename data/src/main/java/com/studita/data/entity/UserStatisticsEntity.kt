package com.studita.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.studita.domain.date.DateTimeFormat
import com.studita.domain.entity.UserStatisticsData
import com.studita.domain.entity.UserStatisticsRowData
import com.google.gson.annotations.SerializedName

data class UserStatisticsEntity(
    @SerializedName("time_type") val timeType: String,
    @SerializedName("obtained_XP") val obtainedXP: Int,
    @SerializedName("time_spent") val timeSpent: Long,
    @SerializedName("completed_exercises") val completedExercises: Int,
    @SerializedName("completed_trainings") val completedTrainings: Int,
    @SerializedName("obtained_achievements") val obtainedAchievements: Int,
    @SerializedName("max_days_streak") val maxDaysStreak: Long,
    @SerializedName("completed_chapters") val completedChapters: Int
)

@Entity(tableName = UserStatisticsRowEntity.TABLE_NAME)
data class UserStatisticsRowEntity(
    @SerializedName("datetime") val datetime: String = "1900-01-01 00:00:00",
    @SerializedName("obtained_XP") val obtainedXP: Int = 0,
    @SerializedName("time_spent") val obtainedTime: Long = 0,
    @SerializedName("completed_exercises") val completedExercises: Int = 0,
    @SerializedName("completed_trainings") val completedTrainings: Int = 0,
    @SerializedName("days_streak") val daysStreak: Long = 0,
    @SerializedName("completed_chapters") val completedChapters: Int = 0
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
