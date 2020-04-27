package com.example.studita.domain.repository

import com.example.studita.domain.entity.exercise.ExerciseData
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.domain.entity.exercise.ExerciseResponseData

interface ExerciseResultRepository {

    suspend fun getExerciseResult(exerciseNumber: Int, exerciseRequestData: ExerciseRequestData): Pair<Int, ExerciseResponseData>

    suspend fun formExerciseResponse(exerciseData: ExerciseData.ExerciseDataExercise, exerciseRequestData: ExerciseRequestData) : ExerciseResponseData

}