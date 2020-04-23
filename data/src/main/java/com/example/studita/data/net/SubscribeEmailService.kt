package com.example.studita.data.net

import com.example.studita.data.entity.UserIdToken
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SubscribeEmailService {
    @POST("subscribe_email")
    fun subscribeEmailAsync(@Body userIdToken: UserIdToken): Deferred<Response<HashMap<String, String>>>

    @POST("unsubscribe_email")
    fun unsubscribeEmailAsync(@Body userIdToken: UserIdToken): Deferred<Response<HashMap<String, String>>>
}