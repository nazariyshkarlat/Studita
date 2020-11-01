package com.example.studita.service

import android.content.Context
import androidx.work.*
import com.example.studita.di.NetworkModule
import com.example.studita.di.data.InterestingModule
import com.example.studita.di.data.exercise.ExerciseResultModule
import com.example.studita.domain.entity.InterestingLikeRequestData
import com.example.studita.domain.entity.exercise.ExerciseReportRequestData
import com.example.studita.domain.service.SyncInterestingLikes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SyncInterestingLikesImpl : SyncInterestingLikes {

    companion object {
        private const val SYNC_INTERESTING_LIKES_ID = "syncInterestingLikesReports"
    }

    override fun scheduleSendInterestingLike(interestingLikeRequestData: InterestingLikeRequestData) {
        val json = serializeInterestingLikeRequestData(interestingLikeRequestData)
        val data = Data.Builder()
        data.putString("INTERESTING_LIKE_DATA", json)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val work = OneTimeWorkRequest.Builder(SendInterestingLikeWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()
        val workManager = WorkManager.getInstance(NetworkModule.context)
        workManager.enqueueUniqueWork(SYNC_INTERESTING_LIKES_ID, ExistingWorkPolicy.APPEND, work)
    }

    class SendInterestingLikeWorker(val context: Context, val params: WorkerParameters) :
        CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            val json = inputData.getString("INTERESTING_LIKE_DATA")

            if (json != null)
                InterestingModule.getInterestingInteractorImpl().sendInterestingLike(deserializeInterestingLikeRequestData(json))

            return Result.success()
        }

        private fun deserializeInterestingLikeRequestData(json: String): InterestingLikeRequestData {
            return Gson().fromJson(
                json,
                TypeToken.get(InterestingLikeRequestData::class.java).type
            )
        }

    }

    private fun serializeInterestingLikeRequestData(interestingLikeRequestData: InterestingLikeRequestData): String {
        return Gson().toJson(interestingLikeRequestData)
    }

}