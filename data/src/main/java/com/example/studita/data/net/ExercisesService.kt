package com.example.studita.data.net

import com.example.studita.data.entity.exercise.ExerciseEntity
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path

interface ExercisesService {

    @GET("chapter_parts/{number}")
    fun getExercisesAsync(@Path("number") chapter_part_number: Int): Deferred<Response<ResponseBody>>

}