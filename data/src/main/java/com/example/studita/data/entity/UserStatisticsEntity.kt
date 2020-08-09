package com.example.studita.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.studita.domain.date.DateTimeFormat
import com.example.studita.domain.entity.UserStatisticsData
import com.example.studita.domain.entity.UserStatisticsRowData
import com.google.gson.annotations.SerializedName

data class UserStatisticsEntity(
    @SerializedName("time_type") val timeType: String,
    @SerializedName("obtained_XP") val obtainedXP: Int,
    @SerializedName("obtained_time") val obtainedTime: Long,
    @SerializedName("obtained_exercises") val obtainedExercises: Int,
    @SerializedName("obtained_trainings") val obtainedTrainings: Int,
    @SerializedName("obtained_achievements") val obtainedAchievements: Int
)

@Entity(tableName = UserStatisticsRowEntity.TABLE_NAME)
data class UserStatisticsRowEntity(
    @SerializedName("datetime") val datetime: String = "1900-01-01 00:00:00",
    @SerializedName("obtained_XP") val obtainedXP: Int? = null,
    @SerializedName("obtained_time") val obtainedTime: Long? = null,
    @SerializedName("obtained_exercises") val obtainedExercises: Int? = null,
    @SerializedName("obtained_trainings") val obtainedTrainings: Int? = null,
    @SerializedName("obtained_achievements") val obtainedAchievements: Int? = null
) {
    companion object {
        const val TABLE_NAME = "user_statistics"
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "record_id")
    @Transient
    var recordId: Int = 0
}

data class SaveUserStatisticsRequest(
    @SerializedName("auth_data") val userIdToken: UserIdToken,
    @SerializedName("user_statistics") val userStatisticsRowEntity: UserStatisticsRowEntity
)

fun UserStatisticsEntity.toBusinessEntity() = UserStatisticsData(
    obtainedXP,
    obtainedTime,
    obtainedExercises,
    obtainedTrainings,
    obtainedAchievements
)

fun UserStatisticsRowEntity.toBusinessEntity() = UserStatisticsRowData(
    DateTimeFormat().parse(datetime)!!,
    obtainedXP,
    obtainedTime,
    obtainedExercises,
    obtainedTrainings,
    obtainedAchievements
)

fun UserStatisticsRowData.toRawEntity() = UserStatisticsRowEntity(
    DateTimeFormat().format(datetime),
    obtainedXP,
    obtainedTime,
    obtainedExercises,
    obtainedTrainings,
    obtainedAchievements
)
