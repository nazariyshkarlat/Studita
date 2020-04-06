package com.example.studita.data.net

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserStatisticsService {

    @POST("user_statistics/{time}")
    fun getUserStatisticsAsync(@Path("time")time: String, @Body IDandToken: HashMap<String, String>) : Deferred<Response<JsonObject>>
}