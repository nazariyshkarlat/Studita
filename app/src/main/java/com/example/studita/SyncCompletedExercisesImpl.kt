package com.example.studita

import android.content.Context
import androidx.work.*
import com.example.studita.di.NetworkModule
import com.example.studita.di.data.CompleteExercisesModule
import com.example.studita.di.data.UserStatisticsModule
import com.example.studita.domain.entity.*
import com.example.studita.domain.service.SyncCompletedExercises
import com.example.studita.utils.UserUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SyncCompletedExercisesImpl : SyncCompletedExercises {

    companion object {
        private const val syncUserStatisticsId = "syncCompleteChapterPart"
    }

    override fun scheduleCompleteExercises(completedExercisesData: CompletedExercisesData) {
        val json = serializeCompletedChapterPartRequest(completedExercisesData)
        val data = Data.Builder()
        data.putString("COMPLETED_CHAPTER_PART", json)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val work = OneTimeWorkRequest.Builder(CompleteChapterPartWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()
        val workManager = WorkManager.getInstance(NetworkModule.context)
        workManager.enqueueUniqueWork(syncUserStatisticsId, ExistingWorkPolicy.APPEND, work)
    }

    class CompleteChapterPartWorker(val context: Context, val params: WorkerParameters) : CoroutineWorker(context, params){
        override suspend fun doWork(): Result {
            val userIdToken = UserUtils.getUserTokenIdData()
            val json = inputData.getString("COMPLETED_CHAPTER_PART")

            if((userIdToken != null) && (json != null))
                CompleteExercisesModule.getCompleteExercisesInteractorImpl().completeExercises(
                    CompleteExercisesRequestData(userIdToken, deserializeCompletedChapterPartRequest(json))
                )

            return Result.success()
        }

        private fun deserializeCompletedChapterPartRequest(json: String): CompletedExercisesData {
            return Gson().fromJson(json, TypeToken.get(CompletedExercisesData::class.java).type)
        }

    }

    private fun serializeCompletedChapterPartRequest(completedExercisesData: CompletedExercisesData): String{
        return Gson().toJson(completedExercisesData)
    }

}