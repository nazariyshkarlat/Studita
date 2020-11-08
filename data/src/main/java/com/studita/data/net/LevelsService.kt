package com.studita.data.net

import com.google.gson.JsonArray
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LevelsService {

    @GET("levels")
    suspend fun getLevels(): Response<JsonArray>
}