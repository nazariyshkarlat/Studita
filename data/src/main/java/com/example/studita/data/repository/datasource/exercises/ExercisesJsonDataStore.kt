package com.example.studita.data.repository.datasource.exercises

interface ExercisesJsonDataStore {

    suspend fun getExercisesJson(chapterPartNumber: Int): Pair<Int, String>

}