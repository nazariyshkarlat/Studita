package com.studita.data.net

import kotlinx.serialization.json.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming

interface OfflineDataService{

    @Streaming
    @GET("offline_data")
    suspend fun getOfflineData(): Response<JsonObject>


}