package com.example.studita.data.net

import com.example.studita.data.entity.SaveUserStatisticsRequest
import com.example.studita.data.entity.UserStatisticsEntity
import com.example.studita.data.entity.UserIdToken
import com.google.gson.JsonArray
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface UserStatisticsService {

    @POST("user_statistics")
    suspend fun getUserStatistics(@Header("Date") dateTime: String, @Body userIdToken: UserIdToken) : Response<JsonArray>

    @POST("save_user_statistics")
    suspend fun saveUserStatistics(@Body saveUserStatisticsRequest: SaveUserStatisticsRequest) : Response<ResponseBody>
}