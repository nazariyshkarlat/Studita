package com.example.studita.data.repository.datasource.exercises.result

import com.example.studita.data.entity.exercise.*
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.net.ExerciseResultService
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ExerciseResultDataStoreImpl(
    private val connectionManager: ConnectionManager,
    private val  exerciseResultService: ExerciseResultService
) : ExerciseResultDataStore {

    private val gsonBuilder = GsonBuilder()
    private val deserializer: ExerciseResponseDescriptionDeserializer = ExerciseResponseDescriptionDeserializer()
    private val descriptionGson: Gson
    private val type: Type

    init {
        gsonBuilder.registerTypeAdapter(ExerciseResponseDescriptionContent::class.java, deserializer)
        descriptionGson = gsonBuilder.create()
        type = object : TypeToken<ExerciseResponseDescriptionContent>() {}.type
    }

    override suspend fun getExerciseResult(
        exerciseNumber: Int,
        exerciseRequestEntity: ExerciseRequestEntity
    ): Pair<Int, ExerciseResponseEntity> {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val exercisesAsync =
                    exerciseResultService.getExerciseResultAsync(
                        exerciseNumber,
                        exerciseRequestEntity
                    )
                val rawResult = exercisesAsync.await()
                val result = ExerciseResponseEntity(
                    rawResult.body()!!.exerciseResult, descriptionGson.fromJson(
                        rawResult.body()!!.description, type
                    )
                )
                return rawResult.code() to result
            }catch (e: Exception) {
            throw ServerUnavailableException()
        }
        }
    }
}