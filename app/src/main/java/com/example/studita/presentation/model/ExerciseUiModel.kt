package com.example.studita.presentation.model

import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.IdRes
import com.example.studita.domain.entity.exercise.ExerciseShapeData

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
    }
}

data class ExerciseShape(@IdRes val shapeId: Int, val count: Int)