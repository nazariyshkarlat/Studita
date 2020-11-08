package com.studita.data.net

import com.studita.data.entity.interesting.InterestingLikeRequest
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface InterestingService {

    @GET("interesting/{number}")
    suspend fun getInteresting(@Path("number") interestingNumber: Int): Response<JsonObject>

}