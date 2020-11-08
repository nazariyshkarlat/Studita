package com.studita.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.studita.data.entity.CompletedExercisesEntity.Companion.TABLE_NAME
import com.studita.domain.date.DateTimeFormat
import com.studita.domain.entity.CompleteExercisesRequestData
import com.studita.domain.entity.CompletedExercisesData
import com.google.gson.annotations.SerializedName

@Entity(tableName = TABLE_NAME)
data class CompletedExercisesEntity(
    @ColumnInfo(name = "chapter_number")
    @SerializedName("chapter_number") val chapterNumber: Int,
    @ColumnInfo(name = "chapter_part_in_chapter_number")
    @SerializedName("chapter_part_in_chapter_number") val chapterPartNumber: Int,
    @ColumnInfo(name = "percent")
    @SerializedName("percent") val percent: Float,
    @ColumnInfo(name = "datetime")
    @SerializedName("datetime") val datetime: String,
    @ColumnInfo(name = "obtained_time")
    @SerializedName("obtained_time") val obtainedTime: Long,
    @ColumnInfo(name = "exercises_bonus_correct_count")
    @SerializedName("exercises_bonus_correct_count") val exercisesBonusCorrectCount: Int
){
    companion object{
        const val TABLE_NAME = "completed_exercises"
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "record_id")
    @Transient
    var recordId: Int = 0
}

data class CompleteExercisesRequest(
    @SerializedName("auth_data") val userIdToken: UserIdToken,
    @SerializedName("completed_exercises_data") val completedExercisesEntity: CompletedExercisesEntity
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