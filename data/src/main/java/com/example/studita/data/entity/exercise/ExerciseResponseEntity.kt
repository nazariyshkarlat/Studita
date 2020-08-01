package com.example.studita.data.entity.exercise

import com.example.studita.domain.entity.exercise.*
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class ExerciseResponseRawEntity(@SerializedName("result")val exerciseResult: Boolean, @SerializedName("description")val description: JsonObject?)

data class ExerciseResponseEntity(val exerciseResult: Boolean, val description: ExerciseResponseDescriptionEntity?)

data class ExerciseResponseDescriptionEntity(val descriptionContent: ExerciseResponseDescriptionContent)

sealed class ExerciseResponseDescriptionContent{
    class DescriptionContentString(val descriptionContent: String) : ExerciseResponseDescriptionContent()
    class DescriptionContentArray(val descriptionContent: List<String>) : ExerciseResponseDescriptionContent()
}

fun ExerciseRequestData.toBusinessEntity() = ExerciseRequestEntity(
    exerciseAnswer
)

fun ExerciseResponseEntity.toBusinessEntity() = ExerciseResponseData(
    exerciseResult,
    description?.toBusinessEntity()
)
fun ExerciseResponseDescriptionEntity.toBusinessEntity() = ExerciseResponseDescriptionData(when(descriptionContent){
    is ExerciseResponseDescriptionContent.DescriptionContentString -> ExerciseResponseDescriptionContentData.DescriptionContentString(descriptionContent.descriptionContent)
    is ExerciseResponseDescriptionContent.DescriptionContentArray -> ExerciseResponseDescriptionContentData.DescriptionContentArray(
        ExerciseImagesRowData( descriptionContent.descriptionContent[0].toImageType(), descriptionContent.descriptionContent[1].toInt())
    )
})