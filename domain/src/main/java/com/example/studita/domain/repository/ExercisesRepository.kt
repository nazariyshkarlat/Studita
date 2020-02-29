package com.example.studita.domain.repository

import com.example.studita.domain.entity.exercise.ExerciseData
import com.example.studita.domain.entity.exercise.ExerciseResponseData
import com.example.studita.domain.entity.exercise.ExercisesResponseData

interface ExercisesRepository {

    suspend fun getExercises(chapterPartNumber: Int): Pair<Int, ExercisesResponseData>

}