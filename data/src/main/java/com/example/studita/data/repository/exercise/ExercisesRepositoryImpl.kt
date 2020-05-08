package com.example.studita.data.repository.exercise

import com.example.studita.data.cache.exercises.ExercisesCache
import com.example.studita.data.entity.mapper.exercise.ExercisesDataMapper
import com.example.studita.data.repository.datasource.exercises.*
import com.example.studita.domain.entity.exercise.ExercisesResponseData
import com.example.studita.domain.repository.ExercisesRepository

class ExercisesRepositoryImpl(private val exercisesDataStoreFactory: ExercisesDataStoreFactory, private val exerciseDataMapper: ExercisesDataMapper):
    ExercisesRepository {

    override suspend fun getExercises(chapterPartNumber: Int, offlineMode: Boolean): Pair<Int, ExercisesResponseData> =
        with(ExercisesDataStoreImpl(exercisesDataStoreFactory.create(if(offlineMode) ExercisesDataStoreFactory.Priority.CACHE else ExercisesDataStoreFactory.Priority.CLOUD)).getExercises(chapterPartNumber)){
            this.first to exerciseDataMapper.map(this.second)
        }

    override suspend fun downloadOfflineExercises(): Int {
        val diskDataStore = (exercisesDataStoreFactory.create(ExercisesDataStoreFactory.Priority.CACHE) as DiskExercisesJsonDataStore)
        return if(!diskDataStore.exercisesAreCached()) {
            val pair = (exercisesDataStoreFactory.create(ExercisesDataStoreFactory.Priority.CLOUD) as CloudExercisesJsonDataStore).getOfflineExercisesJson()
            diskDataStore.saveExercisesJson(
                pair.second
            )
            pair.first
        } else 409
    }
}