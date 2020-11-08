package com.studita.data.net

import com.studita.data.entity.exercise.ExerciseReportEntity
import com.studita.data.entity.exercise.ExerciseReportRequest
import com.studita.data.entity.exercise.ExerciseRequestEntity
import com.studita.data.entity.exercise.ExerciseResponseRawEntity
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ExerciseResultService {

    @POST("exercises/{number}")
    suspend fun getExerciseResult(
        @Path("number") exerciseNumber: Int,
        @Body exerciseRequestEntity: ExerciseRequestEntity
    ): Response<ExerciseResponseRawEntity>

    @POST("send_exercise_report")
    suspend fun sendExerciseReport(
        @Body exerciseReportRequest: ExerciseReportRequest
    ) : Response<ResponseBody>

}