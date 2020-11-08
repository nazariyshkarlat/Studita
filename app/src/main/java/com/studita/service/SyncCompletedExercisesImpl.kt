package com.studita.service

import android.content.Context
import androidx.work.*
import com.studita.di.NetworkModule
import com.studita.di.data.CompleteExercisesModule
import com.studita.domain.entity.CompleteExercisesRequestData
import com.studita.domain.service.SyncCompletedExercises
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SyncCompletedExercisesImpl : SyncCompletedExercises {

    companion object {
        private const val SYNC_COMPLETED_EXERCISES_ID = "syncCompletedExercises"
    }

    override fun scheduleCompleteExercises(completedExercisesRequestData: CompleteExercisesRequestData) {
        val json = serializeCompleteExercisesRequest(completedExercisesRequestData)
        val data = Data.Builder()
        data.putString("COMPLETED_EXERCISES_REQUEST_DATA", json)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val work = OneTimeWorkRequest.Builder(CompleteExercisesWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()
        val workManager = WorkManager.getInstance(NetworkModule.context)
        workManager.enqueueUniqueWork(SYNC_COMPLETED_EXERCISES_ID, ExistingWorkPolicy.APPEND, work)
    }

    class CompleteExercisesWorker(val context: Context, val params: WorkerParameters) :
        CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            val json = inputData.getString("COMPLETED_EXERCISES_REQUEST_DATA")

            if (json != null)
                CompleteExercisesModule.getCompleteExercisesInteractorImpl().completeExercises(
                    deserializeCompleteExercisesRequest(json)
                )

            return Result.success()
        }

        private fun deserializeCompleteExercisesRequest(json: String): CompleteExercisesRequestData {
            return Gson().fromJson(
                json,
                TypeToken.get(CompleteExercisesRequestData::class.java).type
            )
        }

    }

    private fun serializeCompleteExercisesRequest(completeExercisesRequestData: CompleteExercisesRequestData): String {
        return Gson().toJson(completeExercisesRequestData)
    }

}