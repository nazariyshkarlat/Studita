package com.example.studita.data.entity.exercise

import com.google.gson.annotations.SerializedName
 sealed class ExerciseInfo{
        data class ExerciseType1Info(
            @SerializedName("title") val title: String, @SerializedName("subtitle") val subtitle: String, @SerializedName(
                "variants"
            ) val variants: List<List<String>>
        ) : ExerciseInfo()

        data class ExerciseType2Info(
            @SerializedName("title") val title: List<String>, @SerializedName(
                "subtitle"
            ) val subtitle: String, @SerializedName("variants") val variants: List<String>
        ) : ExerciseInfo()
    }

sealed class ScreenInfo{
    data class ScreenType1Info(
        @SerializedName("title") val title: String, @SerializedName("subtitle") val subtitle: String, @SerializedName(
            "parts_to_inject"
        ) val partsToInject: List<String>
    ): ScreenInfo()

    data class ScreenType2Info(@SerializedName("title") val title: String) : ScreenInfo()
}