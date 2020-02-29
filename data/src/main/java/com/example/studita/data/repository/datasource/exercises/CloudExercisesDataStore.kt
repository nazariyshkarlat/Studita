package com.example.studita.data.repository.datasource.exercises

import com.example.studita.data.entity.exercise.ExerciseDeserializer
import com.example.studita.data.entity.exercise.ExerciseEntity
import com.example.studita.data.entity.exercise.ExercisesRawResponse
import com.example.studita.data.entity.exercise.ExercisesResponse
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.net.ExercisesService
import com.example.studita.domain.exception.NetworkConnectionException
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class CloudExercisesDataStore(
    private val connectionManager: ConnectionManager,
    private val  exercisesService: ExercisesService
) : ExercisesDataStore {

    private val gsonBuilder = GsonBuilder()
    private val deserializer: ExerciseDeserializer  = ExerciseDeserializer()
    private val exercisesGson: Gson
    private val type: Type

    init {
        gsonBuilder.registerTypeAdapter(ExerciseEntity::class.java, deserializer)
        exercisesGson = gsonBuilder.create()
        type = object : TypeToken<List<ExerciseEntity>>() {}.type
    }

    override suspend fun getExercises(chapterPartNumber: Int): Pair<Int, ExercisesResponse> =
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            val exercises: List<ExerciseEntity>
            val exercisesAsync = exercisesService.getExercisesAsync(chapterPartNumber)
            val result = exercisesAsync.await()
            val body = result.body()!!
            exercises = exercisesGson.fromJson(body.exercisesRaw.toString(), type)
            result.code() to ExercisesResponse(body.exercisesStartScreen, body.exercisesDescription, exercises)
        }
}