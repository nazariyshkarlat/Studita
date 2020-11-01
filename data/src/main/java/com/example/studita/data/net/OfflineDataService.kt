package com.example.studita.data.net

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming

interface OfflineDataService{

    @Streaming
    @GET("offline_data")
    suspend fun getOfflineData(): Response<JsonObject>


}