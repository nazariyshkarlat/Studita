package com.studita.data.net

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ExercisesService {

    @GET("chapter_parts/{number}")
    suspend fun getExercises(@Path("number") chapterPartNumber: Int): Response<JsonObject>

}