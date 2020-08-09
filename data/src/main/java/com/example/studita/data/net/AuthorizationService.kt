package com.example.studita.data.net

import com.example.studita.data.entity.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthorizationService {

    @POST("authentication")
    suspend fun checkTokenIsCorrect(@Body userIdToken: UserIdToken): Response<Boolean?>

    @POST("sign_out")
    suspend fun signOut(@Body signOutRequestEntity: SignOutRequestEntity): Response<ResponseBody?>

    @POST("log_in")
    suspend fun logIn(@Body authorizationData: AuthorizationRequestEntity): Response<LogInResponseEntity>

    @POST("sign_up")
    suspend fun signUp(@Body authorizationData: AuthorizationRequestEntity): Response<ResponseBody?>

    @POST("sign_in_with_google")
    suspend fun signInWithGoogle(@Body signInWithGoogleRequest: SignInWithGoogleRequest): Response<LogInResponseEntity>
}
