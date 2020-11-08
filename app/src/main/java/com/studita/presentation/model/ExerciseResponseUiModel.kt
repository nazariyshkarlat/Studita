package com.studita.presentation.model

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.studita.R
import com.studita.domain.entity.exercise.*

data class ExerciseResponseUiModel(
    val exerciseResult: Boolean,
    val description: ExerciseResponseDescriptionUiModel?
)

data class ExerciseResponseDescriptionUiModel(val descriptionContent: ExerciseResponseDescriptionContentUiModel)

sealed class ExerciseResponseDescriptionContentUiModel {
    data class DescriptionContentString(val string: String) :
        ExerciseResponseDescriptionContentUiModel()

    data class DescriptionContentArray(val image: ExerciseImagesRowUiModel) :
        ExerciseResponseDescriptionContentUiModel()
}

sealed class ExerciseImagesEquationMemberUiModel
data class ExerciseOperatorUiModel(val operator: Char) : ExerciseImagesEquationMemberUiModel()
data class ExerciseImagesRowUiModel(val image: Drawable, val count: Int) : ExerciseImagesEquationMemberUiModel()

fun ExerciseImagesRowData.toUiModel(context: Context) =
    ExerciseImagesRowUiModel(imageType.getDrawable(context), count)

fun ImageType.getDrawable(context: Context): Drawable = ContextCompat.getDrawable(
    context, when (this) {
        ImageType.EMOJI -> R.drawable.slightly_smiling_face
        ImageType.ALARM_CLOCK_EMOJI -> R.drawable.alarm_clock
    }
)!!

fun ExerciseResponseDescriptionContentData.DescriptionContentString.toUiModel(context: Context) =
    ExerciseResponseDescriptionContentUiModel.DescriptionContentString(
        when (descriptionContent) {
            "true" -> context.getString(R.string.true_variant)
            "false" -> context.getString(R.string.false_variant)
            else -> descriptionContent.replace(
                ",",
                " ${context.resources.getString(R.string.and)} "
            )
        }
    )

fun ExerciseResponseDescriptionData.toUiModel(context: Context) =
    ExerciseResponseDescriptionUiModel(
        when (val descriptionContentData = descriptionContent) {
            is ExerciseResponseDescriptionContentData.DescriptionContentArray -> ExerciseResponseDescriptionContentUiModel.DescriptionContentArray(
                descriptionContentData.descriptionContent.toUiModel(context)
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

fun ExerciseImagesEquationMemberData.toUiModel(context: Context) = when (this) {
    is ExerciseOperatorData -> ExerciseOperatorUiModel(operator.toCharacter())
    is ExerciseImagesRowData -> this.toUiModel(context)
}
