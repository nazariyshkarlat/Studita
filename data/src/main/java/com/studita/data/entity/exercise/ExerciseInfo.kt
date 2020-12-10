package com.studita.data.entity.exercise

import com.google.gson.annotations.SerializedName

sealed class ExerciseInfo {
    data class ExerciseType1Info(
        @SerializedName("title") val title: String,
        @SerializedName("subtitle") val subtitle: String,
        @SerializedName("variants") val variants: List<List<String>>
    ) : ExerciseInfo()

    data class ExerciseType2Info(
        @SerializedName("title") val title: List<String>,
        @SerializedName("subtitle") val subtitle: String,
        @SerializedName("variants") val variants: List<String>
    ) : ExerciseInfo()

    data class ExerciseType3Info(
        @SerializedName("title") val title: List<String>,
        @SerializedName("subtitle") val subtitle: String,
        @SerializedName("variants") val variants: List<String>
    ) : ExerciseInfo()

    data class ExerciseType4Info(
        @SerializedName("title") val title: List<String>,
        @SerializedName("subtitle") val subtitle: String,
        @SerializedName("variants") val variants: List<String>
    ) : ExerciseInfo()

    data class ExerciseType5Info(
        @SerializedName("title") val title: String,
        @SerializedName("subtitle") val subtitle: String,
        @SerializedName("variants") val variants: List<String>
    ) : ExerciseInfo()


    data class ExerciseType6Info(
        @SerializedName("title") val title: String,
        @SerializedName("subtitle") val subtitle: String,
        @SerializedName("variants") val variants: List<String>
    ) : ExerciseInfo()

    data class ExerciseType7Info(@SerializedName("title") val title: String) : ExerciseInfo()

    data class ExerciseType8Info(
        @SerializedName("title") val title: String,
        @SerializedName("subtitle") val subtitle: String,
        @SerializedName("variants") val variants: List<String>
    ) : ExerciseInfo()

    data class ExerciseType12Info(
        @SerializedName("title") val title: String,
        @SerializedName("subtitle") val subtitle: String,
        @SerializedName("variants") val variants: List<String>
    ) : ExerciseInfo()

    data class ExerciseType9Info(@SerializedName("title") val title: String, @SerializedName("subtitle")val subtitle: String) : ExerciseInfo()

    data class ExerciseType10Info(
        @SerializedName("title_parts") val titleParts: List<String>,
        @SerializedName("subtitle") val subtitle: String,
        @SerializedName("is_numeral") val isNumeral: Boolean
    ) : ExerciseInfo()

    data class ExerciseType11Info(
        @SerializedName("title_parts") val titleParts: List<String>,
        @SerializedName("filter") val filter: String,
        @SerializedName("compare_number") val compareNumber: String
    ) : ExerciseInfo()

    data class ExerciseType13Info(
        @SerializedName("title") val title: List<String>,
        @SerializedName("subtitle") val subtitle: String,
        @SerializedName("variants") val variants: List<String>
    ) : ExerciseInfo()

    data class ExerciseType14Info(
        @SerializedName("title") val title: List<String>,
        @SerializedName("subtitle") val subtitle: String,
        @SerializedName("variants") val variants: List<String>
    ) : ExerciseInfo()

    data class ExerciseType15Info(
        @SerializedName("title") val title: String,
        @SerializedName("subtitle") val subtitle: String,
        @SerializedName("variants") val variants: List<String>,
        @SerializedName("correct_count") val correctCount: Int
        ) : ExerciseInfo()

    data class ExerciseType16Info(
        @SerializedName("title_parts") val titleParts: List<String>,
        @SerializedName("subtitle") val subtitle: String
    ) : ExerciseInfo()

    data class ExerciseType17Info(
        @SerializedName("title") val title: List<String>,
        @SerializedName("subtitle") val subtitle: String,
        @SerializedName("variants") val variants: List<List<String>>
    ) : ExerciseInfo()

    data class ExerciseType18Info(
        @SerializedName("title") val title: String,
        @SerializedName("title_images") val titleImages: List<String>
    ) : ExerciseInfo()

    data class ExerciseType19Info(
        @SerializedName("title") val title: String,
        @SerializedName("variants") val variants: List<String>
    ) : ExerciseInfo()

    data class ExerciseType20Info(
        @SerializedName("title") val title: List<List<String>>
    ) : ExerciseInfo()

    data class ExerciseType21Info(
        @SerializedName("title") val title: List<String>,
        @SerializedName("variants") val variants: List<List<String>>
    ) : ExerciseInfo()

    data class ExerciseType22Info(
        @SerializedName("title") val title: String
    ) : ExerciseInfo()

    data class ExerciseType23Info(
        @SerializedName("title_parts") val titleParts: List<String>,
        @SerializedName("subtitle") val subtitle: String,
        @SerializedName("variants") val variants: List<String>
    ) : ExerciseInfo()

    data class ExerciseType24Info(
        @SerializedName("title") val title: String,
        @SerializedName("subtitle") val subtitle: String,
        @SerializedName("variants") val variants: List<String>
    ) : ExerciseInfo()

    data class ExerciseType25Info(
        @SerializedName("title") val title: String,
        @SerializedName("subtitle") val subtitle: String,
        @SerializedName("variants") val variants: List<String>
    ) : ExerciseInfo()

    data class ExerciseType26Info(
        @SerializedName("title") val title: String,
        @SerializedName("subtitle") val subtitle: String
    ) : ExerciseInfo()
}

sealed class ScreenInfo {
    data class ScreenType1Info(
        @SerializedName("title") val title: List<String>,
        @SerializedName("subtitle") val subtitle: String,
        @SerializedName("image") val image: String
    ) : ScreenInfo()

    data class ScreenType2Info(@SerializedName("title") val title: String) : ScreenInfo()

    data class ScreenType3Info(
        @SerializedName("title") val title: String,
        @SerializedName("subtitle") val subtitle: String
    ) : ScreenInfo()

    data class ScreenType4Info(
        @SerializedName("title") val title: String,
        @SerializedName("subtitle") val subtitle: String,
        @SerializedName("bonus_start") val isBonusStart: Boolean = false,
        @SerializedName("bonus_seconds") val bonusSeconds: Long = 0L,
        @SerializedName("image") val image: String
    ) : ScreenInfo()

    data class ScreenType5Info(
        @SerializedName("title") val title: List<String>,
        @SerializedName("variants") val variants: List<String>
    ) : ScreenInfo()
}

data class ExplanationInfo(@SerializedName("text_parts") val textParts: List<String>)