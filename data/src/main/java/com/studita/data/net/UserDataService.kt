package com.studita.data.net

import com.studita.data.entity.SaveUserDataRequest
import com.studita.data.entity.UserDataEntity
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface UserDataService {

    @GET("user_data")
    suspend fun getUserData(
        @Header("Date") date: String,
        @Query("user_id") userId: Int
    ): Response<UserDataEntity>

}