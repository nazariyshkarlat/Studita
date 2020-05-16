package com.example.studita.data.net

import com.example.studita.data.entity.EditProfileRequest
import com.example.studita.data.entity.UserIdToken
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface EditProfileService {
    @POST("edit_profile")
    suspend fun editProfile(@Body editProfileRequest: EditProfileRequest): Response<ResponseBody>
}