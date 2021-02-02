package com.studita.data.net

import com.studita.data.entity.exercise.ExerciseReportRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ExerciseReportService {

    @POST("send_exercise_report")
    suspend fun sendExerciseReport(
        @Body exerciseReportRequest: ExerciseReportRequest
    ) : Response<ResponseBody>

}