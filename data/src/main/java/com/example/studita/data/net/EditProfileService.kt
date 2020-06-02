package com.example.studita.data.net

import com.example.studita.data.entity.EditProfileRequest
import com.example.studita.data.entity.UserIdToken
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface EditProfileService {
    @Multipart
    @POST("edit_profile")
    suspend fun editProfile(@Part("json") jsonPart: RequestBody, @Part newAvatarPart: MultipartBody.Part?): Response<JsonObject>

    @GET("user_name_available")
    suspend fun isUserNameAvailable(@Query("user_name") userName: String): Response<Boolean>
}