package com.example.studita.data.cache.exercises

interface ExercisesCache{
    fun saveExercisesJson(json: String)

    fun getExercises(chapterPartNumber: Int): String?

    fun isCached(): Boolean
}