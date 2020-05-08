package com.example.studita.data.entity

import com.google.gson.annotations.SerializedName

data class CompletedExercisesEntity(@SerializedName("chapter_number")val chapterNumber: Int,
                                    @SerializedName("chapter_part_number")val chapterPartNumber: Int,
                                    @SerializedName("percent")val percent: Float,
                                    @SerializedName("datetime")val datetime: String,
                                    @SerializedName("obtained_time")val obtainedTime: Long)

data class CompleteExercisesRequest(@SerializedName("auth_data")val userIdToken: UserIdToken, @SerializedName("completed_exercises_data")val completedExercisesEntity: CompletedExercisesEntity)