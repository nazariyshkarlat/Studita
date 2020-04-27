package com.example.studita.presentation.model

import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
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
            val variants: List<ExerciseShape>
        ) : ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType2UiModel(
            override val exerciseNumber: Int?,
            val title: ExerciseShape,
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
            @IdRes val image: Int
        ) : ExerciseUiModelScreen(exerciseNumber)

        data class ScreenType2UiModel(override val exerciseNumber: Int?, val title: String) : ExerciseUiModelScreen(exerciseNumber)

        data class ScreenType3UiModel(
            override val exerciseNumber: Int?,
            val title: String, val subtitle: String, val partsToInject: List<String>
        ) : ExerciseUiModelScreen(exerciseNumber)
    }
}

data class ExerciseShape(val shapeId: Int, val count: Int)