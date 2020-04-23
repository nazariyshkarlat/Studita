package com.example.studita.data.repository.exercise

import com.example.studita.data.entity.mapper.exercise.ExerciseRequestDataMapper
import com.example.studita.data.entity.mapper.exercise.ExerciseResponseDataMapper
import com.example.studita.data.repository.datasource.exercises.result.ExerciseResultDataStoreFactory
import com.example.studita.domain.entity.exercise.ExerciseRequestData
import com.example.studita.domain.entity.exercise.ExerciseResponseData
import com.example.studita.domain.repository.ExerciseResultRepository

class ExerciseResultRepositoryImpl(private val exerciseResultDataStoreFactory: ExerciseResultDataStoreFactory, private val exerciseRequestDataMapper: ExerciseRequestDataMapper, private val exerciseResponseDataMapper: ExerciseResponseDataMapper):
    ExerciseResultRepository {

    override suspend fun getExerciseResult(
        exerciseNumber: Int,
        exerciseRequestData: ExerciseRequestData
    ): Pair<Int, ExerciseResponseData> =
        with(exerciseResultDataStoreFactory.create().getExerciseResult(exerciseNumber, exerciseRequestDataMapper.map(exerciseRequestData))){
            this.first to exerciseResponseDataMapper.map(this.second)
        }

}