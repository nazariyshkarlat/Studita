package com.studita.service

import android.content.Context
import androidx.work.*
import com.studita.domain.entity.CompleteExercisesRequestData
import com.studita.domain.service.SyncCompletedExercises
import com.studita.domain.interactor.complete_chapter_part.CompleteExercisesInteractor
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.context.GlobalContext

class SyncCompletedExercisesImpl : SyncCompletedExercises {

    companion object {
        private const val SYNC_COMPLETED_EXERCISES_ID = "syncCompletedExercises"
    }

    override fun scheduleCompleteExercises(completedExercisesRequestData: CompleteExercisesRequestData) {
        val json = Json.encodeToString(completedExercisesRequestData)
        val data = Data.Builder()
        data.putString("COMPLETED_EXERCISES_REQUEST_DATA", json)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val work = OneTimeWorkRequest.Builder(CompleteExercisesWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()
        val workManager = WorkManager.getInstance(GlobalContext.get().get())
        workManager.enqueueUniqueWork(SYNC_COMPLETED_EXERCISES_ID, ExistingWorkPolicy.APPEND, work)
    }

    class CompleteExercisesWorker(val context: Context, val params: WorkerParameters) :
        CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            val json = inputData.getString("COMPLETED_EXERCISES_REQUEST_DATA")

            if (json != null)
                GlobalContext.get().get<CompleteExercisesInteractor>().completeExercises(
                    Json.decodeFromString(json)
                )

            return Result.success()
        }

    }

}