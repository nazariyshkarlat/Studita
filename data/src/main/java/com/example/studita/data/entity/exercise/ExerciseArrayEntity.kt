package com.example.studita.data.entity.exercise

import com.google.gson.annotations.SerializedName

sealed class ExerciseArrayEntity(open val exerciseNumber: Int?) {
    data class ExerciseEntity(
        override val exerciseNumber: Int?,
        @SerializedName(
            "exercise_info"
        ) val exerciseInfo: ExerciseInfo,
        @SerializedName("answer") val exerciseAnswer: String?,
        @SerializedName("is_bonus") val isBonus: Boolean
    ) : ExerciseArrayEntity(exerciseNumber)

    data class ScreenEntity(
        override val exerciseNumber: Int?,
        @SerializedName("screen_info") val screenInfo: ScreenInfo
    ) : ExerciseArrayEntity(exerciseNumber)

    data class ExplanationEntity(
        override val exerciseNumber: Int?,
        @SerializedName("explanation_info") val explanationInfo: ExplanationInfo
    ) : ExerciseArrayEntity(exerciseNumber)
}