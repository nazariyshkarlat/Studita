package com.example.studita.domain.entity.exercise

sealed class ExerciseData(open val exerciseNumber: Int?){
    sealed class ExerciseDataExercise(override val exerciseNumber: Int?): ExerciseData(exerciseNumber) {
        data class ExerciseType1Data(
            override val exerciseNumber: Int?,
            val title: String,
            val subtitle: String,
            val variants: List<ExerciseShapeData>
        ) : ExerciseDataExercise(exerciseNumber)

        data class ExerciseType2Data(
            override val exerciseNumber: Int?,
            val title: ExerciseShapeData,
            val subtitle: String,
            val variants: List<String>
        ) : ExerciseDataExercise(exerciseNumber)

        data class ExerciseType3Data(
            override val exerciseNumber: Int?,
            val title: ExerciseNumberData,
            val subtitle: String,
            val variants: List<ExerciseNumberData>
        ) : ExerciseDataExercise(exerciseNumber)

        data class ExerciseType4Data(
            override val exerciseNumber: Int?,
            val title: ExerciseNumberData,
            val subtitle: String,
            val variants: List<String>
        ) : ExerciseDataExercise(exerciseNumber)

    }
    sealed class ExerciseDataScreen(override val exerciseNumber: Int?): ExerciseData(exerciseNumber) {
        data class ScreenType1Data(
            override val exerciseNumber: Int?,
            val title: String, val subtitle: String, val partsToInject: List<String>, val image: String
        ) : ExerciseDataScreen(exerciseNumber)

        data class ScreenType2Data(override val exerciseNumber: Int?, val title: String) : ExerciseDataScreen(exerciseNumber)

        data class ScreenType3Data(
            override val exerciseNumber: Int?,
            val title: String, val subtitle: String, val partsToInject: List<String>
        ) : ExerciseDataScreen(exerciseNumber)
    }
}