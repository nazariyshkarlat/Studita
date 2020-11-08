package com.studita.data.repository.datasource.exercises

import com.studita.data.entity.exercise.ExercisesResponse

interface ExercisesDataStore {

    suspend fun getExercises(chapterPartNumber: Int): Pair<Int, ExercisesResponse>

}