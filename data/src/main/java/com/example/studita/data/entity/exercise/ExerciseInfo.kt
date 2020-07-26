package com.example.studita.data.entity.exercise

import com.google.gson.annotations.SerializedName
 sealed class ExerciseInfo{
        data class ExerciseType1Info(
            @SerializedName("title") val title: String, @SerializedName("subtitle") val subtitle: String, @SerializedName(
                "variants"
            ) val variants: List<List<String>>
        ) : ExerciseInfo()

        data class ExerciseType2And14Info(
            @SerializedName("title") val title: List<String>, @SerializedName(
                "subtitle"
            ) val subtitle: String, @SerializedName("variants") val variants: List<String>
        ) : ExerciseInfo()

     data class ExerciseType3Info(
         @SerializedName("title") val title: List<String>, @SerializedName(
             "subtitle"
         ) val subtitle: String, @SerializedName("variants") val variants: List<List<String>>
     ) : ExerciseInfo()

     data class ExerciseType4Info(
         @SerializedName("title") val title: List<String>, @SerializedName(
             "subtitle"
         ) val subtitle: String, @SerializedName("variants") val variants: List<String>
     ) : ExerciseInfo()

     data class ExerciseType5And6Info(
         @SerializedName("title") val title: String, @SerializedName(
             "subtitle"
         ) val subtitle: String, @SerializedName("variants") val variants: List<String>
     ) : ExerciseInfo()

     data class ExerciseType7Info(@SerializedName("title") val title: String) : ExerciseInfo()

     data class ExerciseType8And12Info(
         @SerializedName("title") val title: String, @SerializedName(
             "subtitle"
         ) val subtitle: String, @SerializedName("variants") val variants: List<String>
     ) : ExerciseInfo()

     data class ExerciseType9Info(@SerializedName("title") val title: String) : ExerciseInfo()

     data class ExerciseType10Info(@SerializedName("title_parts") val titleParts: List<String>, @SerializedName(
         "subtitle"
     ) val subtitle: String, @SerializedName("is_numeral")val isNumeral: Boolean) : ExerciseInfo()

     data class ExerciseType11Info(@SerializedName("title_parts") val titleParts: List<String>, @SerializedName("filter") val filter: String, @SerializedName("compare_number") val compareNumber: String) : ExerciseInfo()

     data class ExerciseType13Info(@SerializedName("title") val title: List<String>, @SerializedName("subtitle")val subtitle: String, @SerializedName("variants") val variants: List<String>) : ExerciseInfo()

     data class ExerciseType15Info(@SerializedName("title") val title: String, @SerializedName("subtitle")val subtitle: String, @SerializedName("variants") val variants: List<String>) : ExerciseInfo()

     data class ExerciseType16Info(@SerializedName("title_parts") val titleParts: List<String>, @SerializedName("subtitle") val subtitle: String ) : ExerciseInfo()

     data class ExerciseType17Info(@SerializedName("title") val title: List<String>, @SerializedName("subtitle")val subtitle: String, @SerializedName("variants") val variants: List<List<String>>) : ExerciseInfo()
 }

sealed class ScreenInfo{
    data class ScreenType1Info(
        @SerializedName("title") val title: String, @SerializedName("subtitle") val subtitle: String, @SerializedName(
            "parts_to_inject"
        ) val partsToInject: List<String>,
        @SerializedName(
            "image"
        ) val image: String
    ): ScreenInfo()

    data class ScreenType2Info(@SerializedName("title") val title: String) : ScreenInfo()

    data class ScreenType3Info(
        @SerializedName("title") val title: String, @SerializedName("subtitle") val subtitle: String, @SerializedName(
            "parts_to_inject"
        ) val partsToInject: List<String>
    ): ScreenInfo()
}