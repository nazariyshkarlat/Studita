package com.example.studita.data.entity.interesting

import com.google.gson.annotations.SerializedName

data class InterestingEntity(@SerializedName("number")val number : Int, @SerializedName("difficulty_level")val difficultyLevel: Int, @SerializedName("title")val title: String, @SerializedName("subtitle")val subtitle: String,
                             @SerializedName("steps")val steps: List<InterestingEntityStepEntity>, @SerializedName("explanation_parts")val explanationParts: List<String>)

data class InterestingEntityStepEntity(@SerializedName("title")val title: String? = null, @SerializedName("subtitle")val subtitle: String? = null, @SerializedName("specific")val specific: String? = null)