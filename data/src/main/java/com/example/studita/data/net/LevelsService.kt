package com.example.studita.data.net

import com.example.studita.data.entity.LevelEntity
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET

interface LevelsService {

    @GET("levels")
    fun getLevelsAsync() : Deferred<Response<List<LevelEntity>>>
}