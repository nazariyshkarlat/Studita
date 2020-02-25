package com.example.studita.data.entity.exercise

import com.google.gson.annotations.SerializedName

sealed class ExerciseInfo{
    data class ExerciseType1Info(@SerializedName("equation")val equation: List<String>) : ExerciseInfo()
    data class ExerciseType2Info(@SerializedName("equation")val equation: String, @SerializedName("variants")val variants: List<String>) : ExerciseInfo()
    data class ExerciseType3Info(@SerializedName("equation")val equation: String) : ExerciseInfo()
    data class ExerciseType4Info(@SerializedName("expression_parts")val expressionParts: List<String>, @SerializedName("expression_result")val expressionResult: String) : ExerciseInfo()
    data class ExerciseType5Info(@SerializedName("expression_parts")val expressionParts: List<String>, @SerializedName("expression_result")val expressionResult: String, @SerializedName("variants")val variants: List<String>) : ExerciseInfo()
    data class ExerciseType6Info(@SerializedName("result")val result: String) : ExerciseInfo()
    data class ExerciseType7Info(@SerializedName("expression_parts")val expressionParts: List<String>, @SerializedName("expression_result")val expressionResult: String, @SerializedName("variants")val variants: List<String>) : ExerciseInfo()
    data class ExerciseType8Info(@SerializedName("desired_shape")val desiredShape: String, @SerializedName("shapes")val shapes: List<String>) : ExerciseInfo()
    data class ExerciseType9Info(@SerializedName("desired_type")val desiredType: String, @SerializedName("numbers")val numbers: List<String>) : ExerciseInfo()
}