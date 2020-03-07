package com.example.studita.data.entity.exercise

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body

sealed class ExerciseArrayEntity {
    data class ExerciseEntity(
        val exerciseNumber: Int, @SerializedName(
            "exercise_info"
        ) val exerciseInfo: ExerciseInfo
    ): ExerciseArrayEntity()

    data class ScreenEntity(@SerializedName("screen_info") val screenInfo: ScreenInfo) : ExerciseArrayEntity()
}