package com.example.studita.data.net

import com.example.studita.data.entity.ChapterEntity
import com.example.studita.data.entity.ChapterPartEntity
import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ChapterService{

    @GET("chapters/{number}")
    suspend fun getChapter(@Path("number") chapterNumber: Int) : Response<JsonObject>

}