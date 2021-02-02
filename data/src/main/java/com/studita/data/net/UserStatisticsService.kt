package com.studita.data.net

import kotlinx.serialization.json.JsonArray
import retrofit2.Response
import retrofit2.http.*

interface UserStatisticsService {

    @GET("user_statistics")
    suspend fun getUserStatistics(
        @Header("Date") dateTime: String,
        @Query("user_id") userId: Int
    ): Response<JsonArray>

}