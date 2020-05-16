package com.example.studita.data.net

import com.example.studita.data.entity.CompleteExercisesRequest
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CompleteExercisesService {

    @POST("complete_exercises")
    suspend fun completeExercises(@Body completeExercisesRequest: CompleteExercisesRequest) : Response<ResponseBody>

}