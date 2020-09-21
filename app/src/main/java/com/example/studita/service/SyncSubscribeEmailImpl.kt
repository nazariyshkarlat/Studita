package com.example.studita.service

import android.content.Context
import androidx.work.*
import com.example.studita.di.NetworkModule
import com.example.studita.di.data.SubscribeEmailModule
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.SubscribeEmailResultStatus
import com.example.studita.domain.service.SyncSubscribeEmail
import com.example.studita.presentation.view_model.SingleLiveEvent
import com.example.studita.utils.UserUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SyncSubscribeEmailImpl : SyncSubscribeEmail {


    companion object {
        private const val SYNC_SUBSCRIBE_EMAIL_ID = "syncSubscribeEmailId"
        var syncSubscribeEmailLiveData = SingleLiveEvent<SubscribeEmailResultStatus>()
    }

    override fun scheduleSubscribeEmail(subscribe: Boolean, userIdTokenData: UserIdTokenData) {
        val data = Data.Builder()
        data.putBoolean("SUBSCRIBE", subscribe)
        data.putString("USER_ID_TOKEN_DATA", serializeUserIdTokenData(userIdTokenData))

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val work = OneTimeWorkRequest.Builder(SubscribeEmailWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()
        val workManager = WorkManager.getInstance(NetworkModule.context)
        workManager.enqueueUniqueWork("$SYNC_SUBSCRIBE_EMAIL_ID ${userIdTokenData.userId}", ExistingWorkPolicy.REPLACE, work)
    }

    class SubscribeEmailWorker(val context: Context, val params: WorkerParameters) :
        CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            val json = inputData.getString("USER_ID_TOKEN_DATA")
            val subscribe = inputData.getBoolean("SUBSCRIBE", false)

            val userIdToken = json?.let { deserializeUserIdTokenData(it) }
            userIdToken?.let {
                val result = if (subscribe)
                    SubscribeEmailModule.getSubscribeEmailInteractorImpl().subscribe(it)
                else
                    SubscribeEmailModule.getSubscribeEmailInteractorImpl().unsubscribe(it)

                SubscribeEmailModule.getSubscribeEmailInteractorImpl().saveSyncedResult(result)
                syncSubscribeEmailLiveData.postValue(result)
            }

            return Result.success()
        }

        private fun deserializeUserIdTokenData(json: String): UserIdTokenData {
            return Gson().fromJson(json, TypeToken.get(UserIdTokenData::class.java).type)
        }

    }

    private fun serializeUserIdTokenData(userIdTokenData: UserIdTokenData): String {
        return Gson().toJson(userIdTokenData)
    }


}
