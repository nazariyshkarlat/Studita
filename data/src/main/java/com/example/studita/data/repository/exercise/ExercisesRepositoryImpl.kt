package com.example.studita.data.repository.exercise

import com.example.studita.data.entity.exercise.ExercisesStartScreen
import com.example.studita.data.entity.mapper.exercise.ExercisesDataMapper
import com.example.studita.data.repository.datasource.exercises.ExercisesDataStoreFactory
import com.example.studita.domain.entity.exercise.ExercisesResponseData
import com.example.studita.domain.repository.ExercisesRepository

class ExercisesRepositoryImpl(private val exercisesDataStoreFactory: ExercisesDataStoreFactory, private val exerciseDataMapper: ExercisesDataMapper):
    ExercisesRepository {

    override suspend fun getExercises(chapterPartNumber: Int): Pair<Int, ExercisesResponseData> =
        with(exercisesDataStoreFactory.create().getExercises(chapterPartNumber)){
            this.first to exerciseDataMapper.map(this.second)
        }
}