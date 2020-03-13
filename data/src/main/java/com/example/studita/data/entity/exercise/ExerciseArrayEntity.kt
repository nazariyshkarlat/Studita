package com.example.studita.data.entity.exercise

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body

sealed class ExerciseArrayEntity(open val exerciseNumber: Int?) {
    data class ExerciseEntity(
        override val exerciseNumber: Int?,
        @SerializedName(
            "exercise_info"
        ) val exerciseInfo: ExerciseInfo
    ): ExerciseArrayEntity(exerciseNumber)

    data class ScreenEntity(override val exerciseNumber: Int?, @SerializedName("screen_info") val screenInfo: ScreenInfo) : ExerciseArrayEntity(exerciseNumber)
}