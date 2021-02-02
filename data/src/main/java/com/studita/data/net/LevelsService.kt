package com.studita.data.net

import kotlinx.serialization.json.JsonArray
import retrofit2.Response
import retrofit2.http.GET

interface LevelsService {

    @GET("levels")
    suspend fun getLevels(): Response<JsonArray>
}