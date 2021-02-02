package com.studita.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.studita.data.entity.CompletedExercisesEntity.Companion.TABLE_NAME
import com.studita.domain.date.DateTimeFormat
import com.studita.domain.entity.CompleteExercisesRequestData
import com.studita.domain.entity.CompletedExercisesData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Entity(tableName = TABLE_NAME)
data class CompletedExercisesEntity(
    @ColumnInfo(name = "chapter_number")
    @SerialName("chapter_number") val chapterNumber: Int,
    @ColumnInfo(name = "chapter_part_in_chapter_number")
    @SerialName("chapter_part_in_chapter_number") val chapterPartNumber: Int,
    @ColumnInfo(name = "percent")
    @SerialName("percent") val percent: Float,
    @ColumnInfo(name = "datetime")
    @SerialName("datetime") val datetime: String,
    @ColumnInfo(name = "obtained_time")
    @SerialName("obtained_time") val obtainedTime: Long,
    @ColumnInfo(name = "exercises_bonus_correct_count")
    @SerialName("exercises_bonus_correct_count") val exercisesBonusCorrectCount: Int
){
    companion object{
        const val TABLE_NAME = "completed_exercises"
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "record_id")
    @Transient
    var recordId: Int = 0
}

@Serializable
data class CompleteExercisesRequest(
    @SerialName("auth_data") val userIdToken: UserIdToken,
    @SerialName("completed_exercises_data") val completedExercisesEntity: CompletedExercisesEntity
)

fun CompletedExercisesData.toRawEntity() = CompletedExercisesEntity(
    chapterNumber,
    chapterPartNumber,
    percent,
    DateTimeFormat().format(datetime),
    obtainedTime,
    exercisesBonusCorrectCount
)


fun CompletedExercisesEntity.toBusinessEntity() = CompletedExercisesData(
    chapterNumber,
    chapterPartNumber,
    percent,
    DateTimeFormat().parse(datetime)!!,
    obtainedTime,
    exercisesBonusCorrectCount
)

fun CompleteExercisesRequestData.toRawEntity() =
    CompleteExercisesRequest(userIdToken!!.toRawEntity(), completedExercisesData.toRawEntity())