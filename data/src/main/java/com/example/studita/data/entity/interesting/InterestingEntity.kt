package com.example.studita.data.entity.interesting

import com.example.studita.data.entity.UserIdToken
import com.example.studita.data.entity.toRawEntity
import com.example.studita.domain.entity.InterestingData
import com.example.studita.domain.entity.InterestingDataScreen
import com.example.studita.domain.entity.InterestingLikeData
import com.example.studita.domain.entity.InterestingLikeRequestData
import com.google.gson.annotations.SerializedName
import java.io.IOException

data class InterestingEntity(
    @SerializedName("number") val number: Int,
    @SerializedName("difficulty_level") val difficultyLevel: Int,
    @SerializedName("title") val title: String,
    @SerializedName("subtitle") val subtitle: String,
    @SerializedName("steps") val steps: List<InterestingEntityStepEntity>,
    @SerializedName("explanation_parts") val explanationParts: List<String>
)

data class InterestingEntityStepEntity(
    @SerializedName("title") val title: String? = null,
    @SerializedName("subtitle") val subtitle: String? = null,
    @SerializedName("specific") val specific: String? = null
)

fun String.toInterestingSpecific() = when (this) {
    "drum_roll" -> InterestingDataScreen.InterestingDataSpecificDrumRollScreen
    else -> throw IOException("unknown type of interesting specific")
}

fun InterestingEntityStepEntity.toBusinessEntity() = specific?.toInterestingSpecific()
    ?: InterestingDataScreen.InterestingDataStepScreen(title, subtitle)

fun InterestingEntity.toBusinessEntity() = InterestingData(
    number,
    listOf(
        InterestingDataScreen.InterestingDataStartScreen(title, subtitle, difficultyLevel),
        *steps.map { it.toBusinessEntity() }.toTypedArray(),
        InterestingDataScreen.InterestingDataExplanationScreen(explanationParts)
    )
)

data class InterestingLikeRequest(
    @SerializedName("auth_data") val userIdToken: UserIdToken?,
    @SerializedName("like_data") val likeData: InterestingLikeEntity
)

fun InterestingLikeRequestData.toRawEntity() = InterestingLikeRequest(userIdTokenData?.toRawEntity(), interestingLikeData.toRawEntity())

data class InterestingLikeEntity(
    @SerializedName("interesting_number") val interestingNumber: Int,
    @SerializedName("like_it") val likeIt: Boolean
)

fun InterestingLikeData.toRawEntity() = InterestingLikeEntity(interestingNumber, likeIt)