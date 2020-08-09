package com.example.studita.data.repository.datasource.exercises.result

import com.example.studita.data.entity.exercise.ExerciseRequestEntity
import com.example.studita.data.entity.exercise.ExerciseResponseDescriptionContent
import com.example.studita.data.entity.exercise.ExerciseResponseDescriptionDeserializer
import com.example.studita.data.entity.exercise.ExerciseResponseEntity
import com.example.studita.data.net.ExerciseResultService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CancellationException
import java.lang.reflect.Type

class ExerciseResultDataStoreImpl(
    private val connectionManager: ConnectionManager,
    private val exerciseResultService: ExerciseResultService
) : ExerciseResultDataStore {

    private val gsonBuilder = GsonBuilder()
    private val deserializer: ExerciseResponseDescriptionDeserializer =
        ExerciseResponseDescriptionDeserializer()
    private val descriptionGson: Gson
    private val type: Type

    init {
        gsonBuilder.registerTypeAdapter(
            ExerciseResponseDescriptionContent::class.java,
            deserializer
        )
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
                val exerciseResult =
                    exerciseResultService.getExerciseResult(
                        exerciseNumber,
                        exerciseRequestEntity
                    )
                val result = ExerciseResponseEntity(
                    exerciseResult.body()!!.exerciseResult, descriptionGson.fromJson(
                        exerciseResult.body()!!.description, type
                    )
                )
                return exerciseResult.code() to result
        }
    }
}