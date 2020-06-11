package com.example.studita.domain.entity.exercise

data class ExerciseShapeData(val shape: String, val count: Int)

fun List<String>.toExerciseShapeData() = ExerciseShapeData(this[0], this[1].toInt())
