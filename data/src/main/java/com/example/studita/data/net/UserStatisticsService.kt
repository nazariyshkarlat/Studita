package com.example.studita.data.net

import com.example.studita.data.entity.UserStatisticsEntity
import com.example.studita.data.entity.UserTokenId
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface UserStatisticsService {

    @POST("user_statistics/{time}")
    fun getUserStatisticsAsync(@Header("Date") dateTime: String, @Path("time")time: String, @Body userTokenId: UserTokenId) : Deferred<Response<UserStatisticsEntity>>
}