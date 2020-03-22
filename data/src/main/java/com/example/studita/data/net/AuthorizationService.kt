package com.example.studita.data.net

import com.example.studita.data.entity.AuthorizationRequestEntity
import com.example.studita.data.entity.LogInResponseEntity
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface AuthorizationService {
    @POST("log_in")
    fun logInAsync(@Body authorizationData: AuthorizationRequestEntity): Deferred<Response<LogInResponseEntity>>

    @POST("sign_up")
    fun signUpAsync(@Body authorizationData: AuthorizationRequestEntity): Deferred<Response<ResponseBody>>

    @POST("sign_in_with_google")
    fun signInWithGoogleAsync(@Body idToken: HashMap<String, String>): Deferred<Response<LogInResponseEntity>>
}
