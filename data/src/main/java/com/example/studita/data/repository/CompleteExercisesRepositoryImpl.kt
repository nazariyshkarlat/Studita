package com.example.studita.data.repository

import com.example.studita.data.entity.toBusinessEntity
import com.example.studita.data.entity.toRawEntity
import com.example.studita.data.repository.datasource.complete_exercises.CompleteExercisesDataStoreFactory
import com.example.studita.domain.date.DateTimeFormat
import com.example.studita.domain.entity.CompleteExercisesRequestData
import com.example.studita.domain.entity.CompletedExercisesData
import com.example.studita.domain.repository.CompleteExercisesRepository
import java.util.*

class CompleteExercisesRepositoryImpl(private val completeExercisesDataStoreFactory: CompleteExercisesDataStoreFactory) :
    CompleteExercisesRepository {
    override suspend fun completeExercises(completeExercisesRequestData: CompleteExercisesRequestData): Int =
        completeExercisesDataStoreFactory.create()
            .completeExercises(completeExercisesRequestData.toRawEntity())

    override suspend fun getLocalCompletedExercises(): List<CompletedExercisesData>? {
        return completeExercisesDataStoreFactory.create().getLocalCompletedExercises()?.map {
            it.toBusinessEntity()
        }
    }

    override suspend fun deleteLocalCompletedExercises(dateTimeCompleted: Date) {
        completeExercisesDataStoreFactory.create().deleteLocalCompletedExercises(DateTimeFormat().format(dateTimeCompleted))
    }

    override suspend fun clearLocalCompletedExercises() {
        completeExercisesDataStoreFactory.create().clearLocalCompletedExercises()
    }

    override suspend fun addLocalCompletedExercises(completedExercisesData: CompletedExercisesData) {
        completeExercisesDataStoreFactory.create().addLocalCompletedExercises(completedExercisesData.toRawEntity())
    }

}