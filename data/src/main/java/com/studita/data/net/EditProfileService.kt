package com.studita.data.net

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface EditProfileService {
    @Multipart
    @POST("edit_profile")
    suspend fun editProfile(
        @Part("json") jsonPart: RequestBody,
        @Part newAvatarPart: MultipartBody.Part?
    ): Response<JsonObject>

    @GET("is_user_name_available")
    suspend fun isUserNameAvailable(@Query("user_name") userName: String): Response<Boolean>
}