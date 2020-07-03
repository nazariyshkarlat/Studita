package com.example.studita.data.repository.datasource.exercises

import com.example.studita.data.entity.exercise.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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
        val exercises: List<ExerciseArrayEntity> = exercisesGson.fromJson(
            ExercisesJsonArrayMapper.map(exercisesRawResponse.exercisesRaw),
            type
        )
        return pair.first to ExercisesResponse(
            exercisesRawResponse.exercisesStartScreenEntity,
            exercisesRawResponse.exercisesDescriptionEntity,
            exercises
        )
    }
}