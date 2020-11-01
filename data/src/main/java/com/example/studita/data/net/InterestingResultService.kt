package com.example.studita.data.net

import com.example.studita.data.entity.interesting.InterestingLikeRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface InterestingResultService {


    @POST("send_interesting_like")
    suspend fun sendInterestingLike(@Body interestingLikeRequest: InterestingLikeRequest): Response<ResponseBody>

}