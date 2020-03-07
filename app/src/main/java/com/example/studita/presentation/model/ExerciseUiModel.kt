package com.example.studita.presentation.model

import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.IdRes
import com.example.studita.domain.entity.exercise.ExerciseShapeData

sealed class ExerciseUiModel {

    sealed class ExerciseUiModelExercise(open val exerciseNumber: Int) : ExerciseUiModel() {
        data class ExerciseType1UiModel(
            override val exerciseNumber: Int, val title: String,
            val subtitle: String,
            val variants: List<ExerciseShape>
        ) : ExerciseUiModelExercise(exerciseNumber)

        data class ExerciseType2UiModel(
            override val exerciseNumber: Int,
            val title: ExerciseShape,
            val subtitle: String,
            val variants: List<String>
        ) : ExerciseUiModelExercise(exerciseNumber)

        sealed class ExerciseUiModelScreen : ExerciseUiModel() {
            data class ScreenType1UiModel(
                val title: String,
                val subtitle: String,
                val partsToInject: List<String>,
                @IdRes val image: Int
            ) : ExerciseUiModelScreen()

            data class ScreenType2UiModel(val title: String) : ExerciseUiModelScreen()
        }
    }
}

data class ExerciseShape(@IdRes val shapeId: Int, val count: Int)