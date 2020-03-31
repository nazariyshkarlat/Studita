package com.example.studita.data.net

import android.service.autofill.UserData
import com.example.studita.data.entity.AuthorizationRequestEntity
import com.example.studita.data.entity.UserDataEntity
import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserDataService {
    @POST("user_data")
    fun getUserDataAsync(@Body IDandToken: HashMap<String, String>): Deferred<Response<JsonObject>>
}