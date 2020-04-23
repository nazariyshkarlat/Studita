package com.example.studita.data.net

import com.example.studita.data.entity.obtained_exercise_data.ObtainedExerciseDataRequestEntity
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ObtainedExerciseDataService {

    @POST("save_obtained_exercise_data")
    fun saveObtainedExerciseDataAsync(@Body obtainedExerciseDataRequestEntity: ObtainedExerciseDataRequestEntity) : Deferred<Response<ResponseBody>>

}