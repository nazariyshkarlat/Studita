package com.studita.data.repository.datasource.exercises

import com.studita.data.cache.exercises.ExercisesCache
import com.studita.domain.exception.NetworkConnectionException

class DiskExercisesJsonDataStore(private val exercisesCache: ExercisesCache) :
    ExercisesJsonDataStore {

    override suspend fun getExercisesJson(chapterPartNumber: Int): Pair<Int, String> =
        200 to (exercisesCache.getExercises(chapterPartNumber)
            ?: throw NetworkConnectionException())

    fun exercisesAreCached() = exercisesCache.isCached()

    fun saveExercisesJson(json: String) = exercisesCache.saveExercisesJson(json)

}