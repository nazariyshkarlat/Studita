package com.studita.data.repository.exercise

import com.studita.data.entity.exercise.toBusinessEntity
import com.studita.data.repository.datasource.exercises.CloudExercisesJsonDataStore
import com.studita.data.repository.datasource.exercises.DiskExercisesJsonDataStore
import com.studita.data.repository.datasource.exercises.ExercisesDataStoreFactory
import com.studita.data.repository.datasource.exercises.ExercisesDataStoreImpl
import com.studita.domain.entity.exercise.ExercisesResponseData
import com.studita.domain.repository.ExercisesRepository

class ExercisesRepositoryImpl(private val exercisesDataStoreFactory: ExercisesDataStoreFactory) :
    ExercisesRepository {

    override suspend fun getExercises(
        chapterPartNumber: Int,
        offlineMode: Boolean
    ): Pair<Int, ExercisesResponseData> =
        with(
            ExercisesDataStoreImpl(exercisesDataStoreFactory.create(if (offlineMode) ExercisesDataStoreFactory.Priority.CACHE else ExercisesDataStoreFactory.Priority.CLOUD)).getExercises(
                chapterPartNumber
            )
        ) {
            this.first to this.second.toBusinessEntity()
        }

    override suspend fun downloadOfflineExercises(): Int {
        val diskDataStore =
            (exercisesDataStoreFactory.create(ExercisesDataStoreFactory.Priority.CACHE) as DiskExercisesJsonDataStore)
        return if (!diskDataStore.exercisesAreCached()) {
            val pair =
                (exercisesDataStoreFactory.create(ExercisesDataStoreFactory.Priority.CLOUD) as CloudExercisesJsonDataStore).getOfflineExercisesJson()
            diskDataStore.saveExercisesJson(
                pair.second
            )
            pair.first
        } else 409
    }
}