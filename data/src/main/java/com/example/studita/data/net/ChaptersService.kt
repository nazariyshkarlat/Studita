package com.example.studita.data.net

import com.google.gson.JsonArray
import retrofit2.Response
import retrofit2.http.GET

interface ChaptersService {

    @GET("chapters")
    suspend fun getChapters(): Response<JsonArray>

}