package com.example.studita.service

import android.content.Context
import androidx.work.*
import com.example.studita.di.NetworkModule
import com.example.studita.di.data.CompleteExercisesModule
import com.example.studita.domain.entity.CompleteExercisesRequestData
import com.example.studita.domain.service.SyncCompletedExercises
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SyncCompletedExercisesImpl : SyncCompletedExercises {

    companion object {
        private const val SYNC_COMPLETED_EXERCISES_ID = "syncCompleteChapterPart"
    }

    override fun scheduleCompleteExercises(completedExercisesRequestData: CompleteExercisesRequestData) {
        val json = serializeCompletedChapterPartRequest(completedExercisesRequestData)
        val data = Data.Builder()
        data.putString("COMPLETED_EXERCISES_REQUEST_DATA", json)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val work = OneTimeWorkRequest.Builder(CompleteChapterPartWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()
        val workManager = WorkManager.getInstance(NetworkModule.context)
        workManager.enqueueUniqueWork("$SYNC_COMPLETED_EXERCISES_ID ${completedExercisesRequestData.userIdToken!!.userId}", ExistingWorkPolicy.APPEND, work)
    }

    class CompleteChapterPartWorker(val context: Context, val params: WorkerParameters) :
        CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            val json = inputData.getString("COMPLETED_EXERCISES_REQUEST_DATA")

            if (json != null)
                CompleteExercisesModule.getCompleteExercisesInteractorImpl().completeExercises(
                    deserializeCompletedChapterPartRequest(json)
                )

            return Result.success()
        }

        private fun deserializeCompletedChapterPartRequest(json: String): CompleteExercisesRequestData {
            return Gson().fromJson(
                json,
                TypeToken.get(CompleteExercisesRequestData::class.java).type
            )
        }

    }

    private fun serializeCompletedChapterPartRequest(completedExercisesRequestData: CompleteExercisesRequestData): String {
        return Gson().toJson(completedExercisesRequestData)
    }

}