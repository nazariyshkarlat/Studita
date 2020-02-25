package com.example.studita.data.repository.exercise

import com.example.studita.data.entity.mapper.exercise.ExerciseDataMapper
import com.example.studita.data.repository.datasource.exercises.ExercisesDataStoreFactory
import com.example.studita.domain.entity.exercise.ExerciseData
import com.example.studita.domain.entity.exercise.ExercisesResponseData
import com.example.studita.domain.repository.ExercisesRepository

class ExercisesRepositoryImpl(private val exercisesDataStoreFactory: ExercisesDataStoreFactory, private val exerciseDataMapper: ExerciseDataMapper):
    ExercisesRepository {

    override suspend fun getExercises(chapterPartNumber: Int): Pair<Int, List<ExerciseData>> =
        with(exercisesDataStoreFactory.create(ExercisesDataStoreFactory.Priority.CLOUD).getExercises(chapterPartNumber)){
            this.first to exerciseDataMapper.map(this.second)
        }
}