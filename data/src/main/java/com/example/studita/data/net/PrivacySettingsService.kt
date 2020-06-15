package com.example.studita.data.net

import com.example.studita.data.entity.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface PrivacySettingsService {

    @POST("privacy_settings")
    suspend fun getPrivacySettings(@Body userIdToken: UserIdToken): Response<PrivacySettingsEntity>

    @POST("edit_privacy_settings")
    suspend fun editPrivacySettings(@Body privacySettingsRequest: PrivacySettingsRequest): Response<ResponseBody>

    @POST("privacy_duels_exceptions")
    suspend fun getPrivacyDuelsExceptionsList(@Body userIdToken: UserIdToken,
                                              @Query("per_page") perPage: Int,
                                              @Query("page_number") page_number: Int): Response<List<PrivacyDuelsExceptionsEntity>>

    @POST("edit_duels_exceptions")
    suspend fun editDuelsExceptions(@Body editDuelsExceptionsRequest: EditDuelsExceptionsRequest): Response<ResponseBody>

}