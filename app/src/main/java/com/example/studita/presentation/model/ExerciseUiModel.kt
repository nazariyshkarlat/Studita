package com.example.studita.presentation.model

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import com.example.studita.domain.entity.exercise.ExerciseData
import com.example.studita.domain.entity.exercise.ExerciseNumberData
import com.example.studita.domain.entity.exercise.ExerciseShapeData
import com.example.studita.domain.entity.exercise.ExerciseType11Filter

sealed class ExerciseUiModel(open val exerciseNumber: Int?){

    sealed class ExerciseUiModelExercise(override val exerciseNumber: Int?) : ExerciseUiModel(exerciseNumber) {
        data class ExerciseType1UiModel(
            override val exerciseNumber: Int?, val title: String,
            val subtitle: String,
            val variants: List<ExerciseShapeUiModel>
        ) : ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType2UiModel(
            override val exerciseNumber: Int?,
            val title: ExerciseShapeUiModel,
            val subtitle: String,
            val variants: List<String>
        ) : ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType3UiModel(
            override val exerciseNumber: Int?,
            val title: ExerciseNumberData,
            val subtitle: String,
            val variants: List<ExerciseNumberData>
        ) : ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType4UiModel(
            override val exerciseNumber: Int?,
            val title: ExerciseNumberData,
            val subtitle: String,
            val variants: List<String>
        ) : ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType5And6UiModel(
            override val exerciseNumber: Int?,
            val title: String,
            val subtitle: String,
            val variants: List<String>
        ) : ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType7UiModel(override val exerciseNumber: Int?,
                                     val title: String) : ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType8UiModel(override val exerciseNumber: Int?,
                                     val title: String, val subtitle: String, val variants: List<String>
        ) :ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType9UiModel(override val exerciseNumber: Int?, val title: String) : ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType10UiModel(override val exerciseNumber: Int?, val titleParts: List<String>, val subtitle: String) : ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType11UiModel(override val exerciseNumber: Int, val titleParts: List<String>, val filter: ExerciseType11Filter, val compareNumber: String) :  ExerciseUiModel.ExerciseUiModelExercise(exerciseNumber)
    }

    sealed class ExerciseUiModelScreen(override val exerciseNumber: Int?) : ExerciseUiModel(exerciseNumber) {
        data class ScreenType1UiModel(
            override val exerciseNumber: Int?,
            val title: String,
            val subtitle: String,
            val partsToInject: List<String>,
            val image: Drawable
        ) : ExerciseUiModelScreen(exerciseNumber)

        data class ScreenType2UiModel(override val exerciseNumber: Int?, val title: String) : ExerciseUiModelScreen(exerciseNumber)

        data class ScreenType3UiModel(
            override val exerciseNumber: Int?,
            val title: String, val subtitle: String, val partsToInject: List<String>
        ) : ExerciseUiModelScreen(exerciseNumber)
    }
}

fun ExerciseData.toUiModel(context: Context) = when (this) {
    is ExerciseData.ExerciseDataExercise.ExerciseType1Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType1UiModel(exerciseNumber, title, subtitle, variants.map { it.toUiModel(context) })
    is ExerciseData.ExerciseDataExercise.ExerciseType2Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType2UiModel(exerciseNumber, title.toUiModel(context), subtitle, variants)
    is ExerciseData.ExerciseDataExercise.ExerciseType3Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType3UiModel(exerciseNumber, title, subtitle, variants)
    is ExerciseData.ExerciseDataExercise.ExerciseType4Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType4UiModel(exerciseNumber, title, subtitle, variants)
    is ExerciseData.ExerciseDataExercise.ExerciseType5and6Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType5And6UiModel(exerciseNumber, title, subtitle, variants)
    is ExerciseData.ExerciseDataExercise.ExerciseType7Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType7UiModel(exerciseNumber,title)
    is ExerciseData.ExerciseDataExercise.ExerciseType8Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType8UiModel(exerciseNumber, title, subtitle, variants)
    is ExerciseData.ExerciseDataExercise.ExerciseType9Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType9UiModel(exerciseNumber,title)
    is ExerciseData.ExerciseDataExercise.ExerciseType10Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType10UiModel(exerciseNumber,titleParts, subtitle)
    is ExerciseData.ExerciseDataExercise.ExerciseType11Data -> ExerciseUiModel.ExerciseUiModelExercise.ExerciseType11UiModel(exerciseNumber,titleParts, filter, compareNumber)

    is ExerciseData.ExerciseDataScreen.ScreenType1Data -> ExerciseUiModel.ExerciseUiModelScreen.ScreenType1UiModel(exerciseNumber, title, subtitle, partsToInject, image.getShapeByName(context))
    is ExerciseData.ExerciseDataScreen.ScreenType2Data -> ExerciseUiModel.ExerciseUiModelScreen.ScreenType2UiModel(exerciseNumber, title)
    is ExerciseData.ExerciseDataScreen.ScreenType3Data -> ExerciseUiModel.ExerciseUiModelScreen.ScreenType3UiModel(exerciseNumber, title, subtitle, partsToInject)
}