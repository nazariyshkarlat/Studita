package com.studita.data.net

import com.google.gson.JsonArray
import retrofit2.Response
import retrofit2.http.GET

interface OfflineExercisesService {

    @GET("offline_exercises")
    suspend fun getOfflineExercises(): Response<JsonArray>

}