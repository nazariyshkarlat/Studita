package com.example.studita.data.net

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET

interface LevelsService {

    @GET("levels")
    fun getLevelsAsync() : Deferred<Response<JsonArray>>
}