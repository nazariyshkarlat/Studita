package com.example.studita.data.net

import com.example.studita.data.entity.UserDataEntity
import com.example.studita.data.entity.UserIdToken
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface UserDataService {
    @POST("user_data")
    fun getUserDataAsync(@Header("Date")date: String, @Body userIdToken: UserIdToken): Deferred<Response<UserDataEntity>>
}