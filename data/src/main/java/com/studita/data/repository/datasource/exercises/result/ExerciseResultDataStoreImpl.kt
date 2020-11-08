package com.studita.data.repository.datasource.exercises.result

import com.studita.data.entity.CompleteExercisesRequest
import com.studita.data.entity.exercise.*
import com.studita.data.net.ExerciseResultService
import com.studita.data.net.connection.ConnectionManager
import com.studita.data.repository.datasource.interesting.result.InterestingResultDataStore
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.Exception
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
                try {
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
                }catch (e: Exception){
                    throw ServerUnavailableException()
                }
            }
    }

    override suspend fun sendExerciseReport(exerciseReportRequest: ExerciseReportRequest): Int {
            if (connectionManager.isNetworkAbsent()) {
                throw NetworkConnectionException()
            } else {
                try {
                    return exerciseResultService.sendExerciseReport(exerciseReportRequest).code()
                }catch (e: Exception){
                        throw ServerUnavailableException()
                    }
        }
    }

}