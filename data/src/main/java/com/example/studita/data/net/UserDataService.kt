package com.example.studita.data.net

import com.example.studita.data.entity.SaveUserDataRequest
import com.example.studita.data.entity.UserDataEntity
import com.example.studita.data.entity.UserIdToken
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface UserDataService {
    @POST("user_data")
    suspend fun getUserData(@Header("Date")date: String, @Body userIdToken: UserIdToken): Response<UserDataEntity>

    @POST("save_user_data")
    suspend fun saveUserData(@Body saveUserDataRequest: SaveUserDataRequest): Response<ResponseBody>

}