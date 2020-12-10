package com.studita.domain.entity.exercise

data class ExerciseResponseData(
    val exerciseResult: Boolean,
    val description: ExerciseResponseDescriptionData?
)

data class ExerciseResponseDescriptionData(val descriptionContent: ExerciseResponseDescriptionContentData)

sealed class ExerciseResponseDescriptionContentData {
    data class DescriptionContentString(val descriptionContent: String) :
        ExerciseResponseDescriptionContentData()

    data class DescriptionContentArray(val descriptionContent: ExerciseImagesRowData) :
        ExerciseResponseDescriptionContentData()
}