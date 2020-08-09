package com.example.studita.data.net

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface InterestingService {

    @GET("interesting/{number}")
    suspend fun getInteresting(@Path("number") interestingNumber: Int): Response<JsonObject>

}