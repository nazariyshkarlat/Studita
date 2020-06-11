package com.example.studita.domain.entity.exercise

data class ExerciseNumberData(val numberName: String, val number: Int)

fun List<String>.toExerciseNumberData() = ExerciseNumberData(this[0], this[1].toInt())