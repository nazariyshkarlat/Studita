package com.example.studita.data.repository.datasource.exercises.result

import com.example.studita.data.entity.exercise.ExerciseRequestEntity
import com.example.studita.data.entity.exercise.ExerciseResponseEntity
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.net.ExerciseResultService
import com.example.studita.data.repository.datasource.exercises.ExercisesDataStore
import com.example.studita.domain.exception.NetworkConnectionException

class CloudExerciseResultDataStore(
    private val connectionManager: ConnectionManager,
    private val  exerciseResultService: ExerciseResultService
) : ExerciseResultDataStore {

    override suspend fun getExerciseResult(
        exerciseNumber: Int,
        exerciseRequestEntity: ExerciseRequestEntity
    ): Pair<Int, ExerciseResponseEntity> {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            val exercisesAsync =
                exerciseResultService.getExerciseResultAsync(exerciseNumber, exerciseRequestEntity)
            val result = exercisesAsync.await()
            return result.code() to result.body()!!
        }
    }
}