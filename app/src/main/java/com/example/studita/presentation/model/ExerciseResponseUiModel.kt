package com.example.studita.presentation.model

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import com.example.studita.R
import com.example.studita.domain.entity.exercise.ExerciseResponseData
import com.example.studita.domain.entity.exercise.ExerciseResponseDescriptionContentData
import com.example.studita.domain.entity.exercise.ExerciseResponseDescriptionData
import com.example.studita.domain.entity.exercise.ExerciseShapeData
import java.io.IOException

data class ExerciseResponseUiModel(val exerciseResult: Boolean, val description: ExerciseResponseDescriptionUiModel?)

data class ExerciseResponseDescriptionUiModel(val descriptionContent: ExerciseResponseDescriptionContentUiModel)

sealed  class ExerciseResponseDescriptionContentUiModel{
    data class DescriptionContentString(val string: String) : ExerciseResponseDescriptionContentUiModel()
    data class DescriptionContentArray(val shape: ExerciseShapeUiModel) : ExerciseResponseDescriptionContentUiModel()
}

data class ExerciseShapeUiModel(val shape: Drawable, val count: Int)

fun ExerciseShapeData.toUiModel(context: Context, white: Boolean = false) = ExerciseShapeUiModel(shape.getShapeByName(context, white), count)

fun String.getShapeByName(context: Context, white: Boolean = false) : Drawable = when(this){
    "rect" -> if(white) context.getDrawable(R.drawable.exercise_rectangle_white)!! else context.getDrawable(R.drawable.exercise_rectangle)!!
    else -> throw IOException("Unexpected shape name")
}

fun ExerciseResponseDescriptionContentData.DescriptionContentString.toUiModel(context: Context) = ExerciseResponseDescriptionContentUiModel.DescriptionContentString(when (descriptionContent) {
    "true" -> context.getString(R.string.true_variant)
    "false" -> context.getString(R.string.false_variant)
    else -> descriptionContent
})

fun ExerciseResponseDescriptionData.toUiModel(context: Context) =  ExerciseResponseDescriptionUiModel(
    when (val descriptionContentData = descriptionContent) {
        is ExerciseResponseDescriptionContentData.DescriptionContentArray -> ExerciseResponseDescriptionContentUiModel.DescriptionContentArray(
            descriptionContentData.descriptionContent.toUiModel(context, true)
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
