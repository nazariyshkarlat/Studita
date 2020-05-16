package com.example.studita.data.net

import com.example.studita.data.entity.*
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface AuthorizationService {
    @POST("sign_out")
    suspend fun signOut(@Body userIdToken: UserIdToken): Response<ResponseBody?>

    @POST("log_in")
    suspend fun logIn(@Body authorizationData: AuthorizationRequestEntity): Response<LogInResponseEntity>

    @POST("sign_up")
    suspend fun signUp(@Body authorizationData: AuthorizationRequestEntity): Response<ResponseBody?>

    @POST("sign_in_with_google")
    suspend fun signInWithGoogle(@Body signInWithGoogleRequestEntity: SignInWithGoogleRequestEntity): Response<LogInResponseEntity>
}
