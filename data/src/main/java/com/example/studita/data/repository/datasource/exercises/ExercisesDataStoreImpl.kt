package com.example.studita.data.repository.datasource.exercises

import com.example.studita.data.entity.exercise.*
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.data.net.ExercisesService
import com.example.studita.domain.exception.NetworkConnectionException
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ExercisesDataStoreImpl(private val exercisesJsonDataStore: ExercisesJsonDataStore) : ExercisesDataStore {

    private val gsonBuilder = GsonBuilder()
    private val deserializer: ExerciseArrayEntityDeserializer  = ExerciseArrayEntityDeserializer()
    private val exercisesGson: Gson
    private val type: Type

    init {
        gsonBuilder.registerTypeAdapter(ExerciseArrayEntity::class.java, deserializer)
        exercisesGson = gsonBuilder.create()
        type = object : TypeToken<List<ExerciseArrayEntity>>() {}.type
    }

    override suspend fun getExercises(chapterPartNumber: Int): Pair<Int, ExercisesResponse> {
        val pair = exercisesJsonDataStore.getExercisesJson(chapterPartNumber)
        val exercisesRawResponse: ExercisesRawResponse =
            Gson().fromJson(pair.second, TypeToken.get(ExercisesRawResponse::class.java).type)
        println(exercisesRawResponse.exercisesRaw)
        val exercises: List<ExerciseArrayEntity> = exercisesGson.fromJson(
            ExercisesJsonArrayMapper.map(exercisesRawResponse.exercisesRaw),
            type
        )
        return pair.first to ExercisesResponse(
            exercisesRawResponse.exercisesStartScreen,
            exercisesRawResponse.exercisesDescription,
            exercises
        )
    }
}