package com.studita.service

import android.content.Context
import androidx.work.*
import com.studita.domain.entity.exercise.ExerciseReportRequestData
import com.studita.domain.service.SyncExercisesReports
import com.studita.domain.interactor.exercises.ExerciseReportnteractor
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.context.GlobalContext
import org.koin.core.context.GlobalContext.get

class SyncExercisesReportsImpl : SyncExercisesReports {

    companion object {
        private const val SYNC_EXERCISES_REPORTS_ID = "syncExercisesReports"
    }

    override fun scheduleSendExerciseReport(exerciseReportRequestData: ExerciseReportRequestData) {
        val json = Json.encodeToString(exerciseReportRequestData)
        val data = Data.Builder()
        data.putString("EXERCISE_REPORT_DATA", json)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val work = OneTimeWorkRequest.Builder(SendExerciseReportWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()
        val workManager = WorkManager.getInstance(get().get())
        workManager.enqueueUniqueWork(SYNC_EXERCISES_REPORTS_ID, ExistingWorkPolicy.APPEND, work)
    }

    class SendExerciseReportWorker(val context: Context, val params: WorkerParameters) :
        CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            val json = inputData.getString("EXERCISE_REPORT_DATA")

            if (json != null)
                GlobalContext.get().get<ExerciseReportnteractor>().sendExerciseReport(Json.decodeFromString(json))

            return Result.success()
        }

    }
}