package com.studita.domain.entity.exercise

import kotlinx.serialization.Serializable

@Serializable
data class ExerciseResponseData(
    val exerciseResult: Boolean,
    val description: ExerciseResponseDescriptionData?
)

@Serializable
data class ExerciseResponseDescriptionData(val descriptionContent: ExerciseResponseDescriptionContentData)

@Serializable
sealed class ExerciseResponseDescriptionContentData {

    @Serializable
    data class DescriptionContentString(val descriptionContent: String) :
        ExerciseResponseDescriptionContentData()

    @Serializable
    data class DescriptionContentArray(val descriptionContent: ExerciseImagesRowData) :
        ExerciseResponseDescriptionContentData()
}