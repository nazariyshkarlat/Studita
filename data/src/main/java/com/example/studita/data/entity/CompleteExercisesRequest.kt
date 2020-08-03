package com.example.studita.data.entity

import com.example.studita.domain.date.DateTimeFormat
import com.example.studita.domain.entity.CompleteExercisesRequestData
import com.example.studita.domain.entity.CompletedExercisesData
import com.google.gson.annotations.SerializedName

data class CompletedExercisesEntity(@SerializedName("chapter_number")val chapterNumber: Int,
                                    @SerializedName("chapter_part_number")val chapterPartNumber: Int,
                                    @SerializedName("percent")val percent: Float,
                                    @SerializedName("datetime")val datetime: String,
                                    @SerializedName("obtained_time")val obtainedTime: Long,
                                    @SerializedName("exercises_bonus_correct_count") val exercisesBonusCorrectCount: Int)

data class CompleteExercisesRequest(@SerializedName("auth_data")val userIdToken: UserIdToken, @SerializedName("completed_exercises_data")val completedExercisesEntity: CompletedExercisesEntity)

fun CompletedExercisesData.toRawEntity() = CompletedExercisesEntity(chapterNumber, chapterPartNumber, percent, DateTimeFormat().format(datetime), obtainedTime, exercisesBonusCorrectCount)
fun CompleteExercisesRequestData.toRawEntity() = CompleteExercisesRequest(userIdToken!!.toRawEntity(), completedExercisesEntity.toRawEntity())