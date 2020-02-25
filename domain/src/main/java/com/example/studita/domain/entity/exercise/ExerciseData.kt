package com.example.studita.domain.entity.exercise

sealed class ExerciseData{
    data class ExerciseType1(val exerciseNumber: Int, val equation: List<String>) : ExerciseData()
    data class ExerciseType2(val exerciseNumber: Int, val equation: String, val variants: List<String>) : ExerciseData()
    data class ExerciseType3(val exerciseNumber: Int, val equation: String) : ExerciseData()
    data class ExerciseType4(val exerciseNumber: Int, val expressionParts: List<String>, val expressionResult: String) : ExerciseData()
    data class ExerciseType5(val exerciseNumber: Int, val expressionParts: List<String>, val expressionResult: String, val variants: List<String>) : ExerciseData()
    data class ExerciseType6(val exerciseNumber: Int, val result: String) : ExerciseData()
    data class ExerciseType7(val exerciseNumber: Int, val expressionParts: List<String>, val expressionResult: String, val variants: List<String>) : ExerciseData()
    data class ExerciseType8(val exerciseNumber: Int, val desiredShape: String, val shapes: List<String>) : ExerciseData()
    data class ExerciseType9(val exerciseNumber: Int, val desiredType: String, val numbers: List<String>) : ExerciseData()
}