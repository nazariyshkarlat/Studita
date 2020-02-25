package com.example.studita.data.net

import com.example.studita.data.entity.exercise.ExerciseRequestEntity
import com.example.studita.data.entity.exercise.ExerciseResponseEntity
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ExerciseResultService {

    @POST("exercises/{number}")
    fun getExerciseResultAsync(@Path("number") exerciseNumber: Int, @Body exerciseRequestEntity: ExerciseRequestEntity): Deferred<Response<ExerciseResponseEntity>>

}