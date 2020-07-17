package com.example.studita.presentation.model

import android.content.Context
import android.graphics.drawable.Drawable
import com.example.studita.R
import com.example.studita.domain.entity.exercise.*
import java.io.IOException

data class ExerciseResponseUiModel(val exerciseResult: Boolean, val description: ExerciseResponseDescriptionUiModel?)

data class ExerciseResponseDescriptionUiModel(val descriptionContent: ExerciseResponseDescriptionContentUiModel)

sealed  class ExerciseResponseDescriptionContentUiModel{
    data class DescriptionContentString(val string: String) : ExerciseResponseDescriptionContentUiModel()
    data class DescriptionContentArray(val shape: ExerciseShapeUiModel) : ExerciseResponseDescriptionContentUiModel()
}

sealed class ExerciseShapeEquationMemberUiModel
data class ExerciseOperatorUiModel(val operator: Char): ExerciseShapeEquationMemberUiModel()
data class ExerciseShapeUiModel(val shape: Drawable, val count: Int): ExerciseShapeEquationMemberUiModel()

fun ExerciseShapeData.toShapeUiModel(context: Context, white: Boolean = false) = ExerciseShapeUiModel(shape.getShapeByName(context, white), count)

fun String.getShapeByName(context: Context, white: Boolean = false) : Drawable = when(this){
    "rect" -> if(white) context.getDrawable(R.drawable.exercise_rectangle_white)!! else context.getDrawable(R.drawable.exercise_rectangle_green)!!
    else -> throw IOException("Unexpected shape name")
}

fun ExerciseResponseDescriptionContentData.DescriptionContentString.toUiModel(context: Context) = ExerciseResponseDescriptionContentUiModel.DescriptionContentString(when (descriptionContent) {
    "true" -> context.getString(R.string.true_variant)
    "false" -> context.getString(R.string.false_variant)
    else -> descriptionContent.replace(",", " ${context.resources.getString(R.string.and)} ")
})

fun ExerciseResponseDescriptionData.toUiModel(context: Context) =  ExerciseResponseDescriptionUiModel(
    when (val descriptionContentData = descriptionContent) {
        is ExerciseResponseDescriptionContentData.DescriptionContentArray -> ExerciseResponseDescriptionContentUiModel.DescriptionContentArray(
            descriptionContentData.descriptionContent.toShapeUiModel(context, true)
        )
        is ExerciseResponseDescriptionContentData.DescriptionContentString -> {
            descriptionContentData.toUiModel(context)
        }
    }
)

fun ExerciseResponseData.toUiModel(context: Context) = ExerciseResponseUiModel(
    exerciseResult,
    description?.toUiModel(context)
)

fun ExerciseShapeEquationMemberData.toUiModel(context: Context) = when(this){
    is ExerciseOperatorData -> ExerciseOperatorUiModel(operator.toCharacter())
    is ExerciseShapeData -> this.toShapeUiModel(context, false)
}
