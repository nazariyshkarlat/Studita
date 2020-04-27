package com.example.studita.data.repository.exercise

import com.example.studita.data.cache.exercises.ExercisesCache
import com.example.studita.data.entity.mapper.exercise.ExercisesDataMapper
import com.example.studita.data.repository.datasource.exercises.*
import com.example.studita.domain.entity.exercise.ExercisesResponseData
import com.example.studita.domain.repository.ExercisesRepository

class ExercisesRepositoryImpl(private val exercisesDataStoreFactory: ExercisesDataStoreFactory, private val exerciseDataMapper: ExercisesDataMapper, private val exercisesCache: ExercisesCache):
    ExercisesRepository {

    override suspend fun getExercises(chapterPartNumber: Int): Pair<Int, ExercisesResponseData> =
        with(ExercisesDataStoreImpl(exercisesDataStoreFactory.create(ExercisesDataStoreFactory.Priority.CACHE)).getExercises(chapterPartNumber)){
            this.first to exerciseDataMapper.map(this.second)
        }

    override suspend fun downloadOfflineExercises(): Int {
        return if(!exercisesCache.isCached()) {
            val pair = (exercisesDataStoreFactory.create(ExercisesDataStoreFactory.Priority.CLOUD) as CloudExercisesJsonDataStore).getOfflineExercisesJson()
            exercisesCache.saveExercisesJson(
                pair.second
            )
            pair.first
        } else 409
    }
}