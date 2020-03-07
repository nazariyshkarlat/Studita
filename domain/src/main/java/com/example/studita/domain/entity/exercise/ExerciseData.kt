package com.example.studita.domain.entity.exercise

sealed class ExerciseData{
    sealed class ExerciseDataExercise(open val exerciseNumber: Int): ExerciseData() {
        data class ExerciseType1Data(
            override val exerciseNumber: Int,
            val title: String,
            val subtitle: String,
            val variants: List<ExerciseShapeData>
        ) : ExerciseDataExercise(exerciseNumber)

        data class ExerciseType2Data(
            override val exerciseNumber: Int,
            val title: ExerciseShapeData,
            val subtitle: String,
            val variants: List<String>
        ) : ExerciseDataExercise(exerciseNumber)
    }
    sealed class ExerciseDataScreen: ExerciseData() {
        data class ScreenType1Data(
            val title: String, val subtitle: String, val partsToInject: List<String>, val image: String
        ) : ExerciseDataScreen()

        data class ScreenType2Data(val title: String) : ExerciseDataScreen()
    }
}