package com.example.studita.data.repository.datasource.exercises

import com.example.studita.data.entity.exercise.ExerciseEntity

interface ExercisesDataStore {

    suspend fun getExercises(chapterPartNumber: Int) : Pair<Int, List<ExerciseEntity>>

}