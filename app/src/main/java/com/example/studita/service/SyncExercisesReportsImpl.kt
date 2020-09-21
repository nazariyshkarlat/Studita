package com.example.studita.service

import android.content.Context
import androidx.work.*
import com.example.studita.di.NetworkModule
import com.example.studita.di.data.CompleteExercisesModule
import com.example.studita.di.data.exercise.ExerciseResultModule
import com.example.studita.domain.entity.CompleteExercisesRequestData
import com.example.studita.domain.entity.exercise.ExerciseReportData
import com.example.studita.domain.entity.exercise.ExerciseReportRequestData
import com.example.studita.domain.service.SyncCompletedExercises
import com.example.studita.domain.service.SyncExercisesReports
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SyncExercisesReportsImpl : SyncExercisesReports {

    companion object {
        private const val SYNC_EXERCISES_REPORTS_ID = "syncExercisesReports"
    }

    override fun scheduleSendExerciseReport(exerciseReportRequestData: ExerciseReportRequestData) {
        val json = serializeExerciseReportRequestData(exerciseReportRequestData)
        val data = Data.Builder()
        data.putString("EXERCISE_REPORT_DATA", json)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val work = OneTimeWorkRequest.Builder(SendExerciseReportWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()
        val workManager = WorkManager.getInstance(NetworkModule.context)
        workManager.enqueueUniqueWork("$SYNC_EXERCISES_REPORTS_ID ${exerciseReportRequestData.userIdTokenData?.userId ?: System.currentTimeMillis()}", ExistingWorkPolicy.APPEND, work)
    }

    class SendExerciseReportWorker(val context: Context, val params: WorkerParameters) :
        CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            val json = inputData.getString("EXERCISE_REPORT_DATA")

            if (json != null)
                ExerciseResultModule.getExerciseResultInteractorImpl().sendExerciseReport(deserializeExerciseReportRequestData(json))

            return Result.success()
        }

        private fun deserializeExerciseReportRequestData(json: String): ExerciseReportRequestData {
            return Gson().fromJson(
                json,
                TypeToken.get(ExerciseReportRequestData::class.java).type
            )
        }

    }

    private fun serializeExerciseReportRequestData(exerciseReportRequestData: ExerciseReportRequestData): String {
        return Gson().toJson(exerciseReportRequestData)
    }

}