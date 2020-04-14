package com.example.studita.data.net

import com.example.studita.data.entity.UserTokenId
import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SubscribeEmailService {
    @POST("subscribe_email")
    fun subscribeEmailAsync(@Body userTokenId: UserTokenId): Deferred<Response<HashMap<String, String>>>

    @POST("unsubscribe_email")
    fun unsubscribeEmailAsync(@Body userTokenId: UserTokenId): Deferred<Response<HashMap<String, String>>>
}