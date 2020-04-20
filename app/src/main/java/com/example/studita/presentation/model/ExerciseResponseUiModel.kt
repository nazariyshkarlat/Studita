package com.example.studita.presentation.model

import android.graphics.drawable.Drawable

data class ExerciseResponseUiModel(val exerciseResult: Boolean, val description: ExerciseResponseDescriptionUiModel?)

data class ExerciseResponseDescriptionUiModel(val descriptionContent: ExerciseResponseDescriptionContentUiModel)

sealed  class ExerciseResponseDescriptionContentUiModel{
    data class DescriptionContentString(val string: String) : ExerciseResponseDescriptionContentUiModel()
    data class DescriptionContentArray(val shape: ExerciseShapeUiModel) : ExerciseResponseDescriptionContentUiModel()
}

data class ExerciseShapeUiModel(val shape: Drawable, val count: Int)