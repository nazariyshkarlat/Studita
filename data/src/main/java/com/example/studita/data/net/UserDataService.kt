package com.example.studita.data.net

import com.example.studita.data.entity.SaveUserDataRequest
import com.example.studita.data.entity.UserDataEntity
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface UserDataService {
    @GET("user_data")
    suspend fun getUserData(
        @Header("Date") date: String,
        @Query("user_id") userId: Int
    ): Response<UserDataEntity>

    @POST("save_user_data")
    suspend fun saveUserData(@Body saveUserDataRequest: SaveUserDataRequest): Response<ResponseBody>

}