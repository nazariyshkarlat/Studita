package com.example.studita.data.repository.datasource.exercises.result

import com.example.studita.data.entity.exercise.ExerciseRequestEntity
import com.example.studita.data.entity.exercise.ExerciseResponseEntity

interface ExerciseResultDataStore {

    suspend fun getExerciseResult(exerciseNumber: Int, exerciseRequestEntity: ExerciseRequestEntity) : Pair<Int, ExerciseResponseEntity>

}