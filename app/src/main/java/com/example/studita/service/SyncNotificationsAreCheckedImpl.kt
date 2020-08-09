package com.example.studita.service

import android.content.Context
import androidx.work.*
import com.example.studita.di.NetworkModule
import com.example.studita.di.data.AuthorizationModule
import com.example.studita.di.data.NotificationsModule
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.service.SyncNotificationsAreChecked
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SyncNotificationsAreCheckedImpl : SyncNotificationsAreChecked {


    companion object {
        private const val SYNC_NOTIFICATIONS_ARE_CHECKED_ID = "syncNotificationsAreChecked"
    }

    override fun scheduleCheckNotifications(userIdTokenData: UserIdTokenData) {
        val data = Data.Builder()
        data.putString("USER_ID_TOKEN_DATA", serializeUserIdTokenData(userIdTokenData))

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val work = OneTimeWorkRequest.Builder(CheckNotificationsWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()
        val workManager = WorkManager.getInstance(NetworkModule.context)
        workManager.enqueueUniqueWork(SYNC_NOTIFICATIONS_ARE_CHECKED_ID, ExistingWorkPolicy.REPLACE, work)
    }

    class CheckNotificationsWorker(val context: Context, val params: WorkerParameters) :
        CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {

            val json = inputData.getString("USER_ID_TOKEN_DATA")
            if (json != null) {
                val userIdTokenData =
                    deserializeUserIdTokenData(json)
                NotificationsModule.getNotificationsInteractorImpl().setNotificationsAreChecked(userIdTokenData)
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
