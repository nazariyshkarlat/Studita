package com.studita.data.net

import com.studita.data.entity.UserIdToken
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SubscribeEmailService {
    @POST("subscribe_email")
    suspend fun subscribeEmail(@Body userIdToken: UserIdToken): Response<HashMap<String, String>>

    @POST("unsubscribe_email")
    suspend fun unsubscribeEmail(@Body userIdToken: UserIdToken): Response<HashMap<String, String>>
}