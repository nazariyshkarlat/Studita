package com.studita.data.net

import kotlinx.serialization.json.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ChapterService {

    @GET("chapters/{number}")
    suspend fun getChapter(@Path("number") chapterNumber: Int): Response<JsonObject>

}