package com.example.studita.data.entity.interesting

import com.example.studita.domain.entity.InterestingData
import com.example.studita.domain.entity.InterestingDataScreen
import com.google.gson.annotations.SerializedName
import java.io.IOException

data class InterestingEntity(@SerializedName("number")val number : Int, @SerializedName("difficulty_level")val difficultyLevel: Int, @SerializedName("title")val title: String, @SerializedName("subtitle")val subtitle: String,
                             @SerializedName("steps")val steps: List<InterestingEntityStepEntity>, @SerializedName("explanation_parts")val explanationParts: List<String>)

data class InterestingEntityStepEntity(@SerializedName("title")val title: String? = null, @SerializedName("subtitle")val subtitle: String? = null, @SerializedName("specific")val specific: String? = null)

fun String.toInterestingSpecific() = when(this){
    "drum_roll" -> InterestingDataScreen.InterestingDataSpecificDrumRollScreen
    else -> throw IOException("unknown type of interesting specific")
}
fun InterestingEntityStepEntity.toBusinessEntity() = specific?.toInterestingSpecific()
    ?: InterestingDataScreen.InterestingDataStepScreen(title, subtitle)
fun InterestingEntity.toBusinessEntity() = InterestingData(number, listOf(InterestingDataScreen.InterestingDataStartScreen(title, subtitle, difficultyLevel), *steps.map { it.toBusinessEntity() }.toTypedArray(), InterestingDataScreen.InterestingDataExplanationScreen(explanationParts)))