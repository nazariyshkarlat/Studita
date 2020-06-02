package com.example.studita.data.net

import com.example.studita.data.entity.PrivacySettingsEntity
import com.example.studita.data.entity.PrivacySettingsRequest
import com.example.studita.data.entity.UserIdToken
import com.example.studita.domain.entity.PrivacySettingsRequestData
import com.google.gson.JsonArray
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PrivacySettingsService {

    @POST("privacy_settings")
    suspend fun getPrivacySettings(@Body userIdToken: UserIdToken): Response<PrivacySettingsEntity>

    @POST("edit_privacy_settings")
    suspend fun editPrivacySettings(@Body privacySettingsRequest: PrivacySettingsRequest): Response<ResponseBody>
}