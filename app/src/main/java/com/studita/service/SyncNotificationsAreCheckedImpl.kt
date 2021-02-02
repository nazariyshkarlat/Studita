package com.studita.service

import android.content.Context
import androidx.work.*
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.service.SyncNotificationsAreChecked
import com.studita.domain.interactor.notifications.NotificationsInteractor
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.context.GlobalContext

class SyncNotificationsAreCheckedImpl : SyncNotificationsAreChecked {


    companion object {
        private const val SYNC_NOTIFICATIONS_ARE_CHECKED_ID = "syncNotificationsAreChecked"
    }

    override fun scheduleCheckNotifications(userIdTokenData: UserIdTokenData) {
        val data = Data.Builder()
        data.putString("USER_ID_TOKEN_DATA", Json.encodeToString(userIdTokenData))

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val work = OneTimeWorkRequest.Builder(CheckNotificationsWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()
        val workManager = WorkManager.getInstance(GlobalContext.get().get())
        workManager.enqueueUniqueWork("$SYNC_NOTIFICATIONS_ARE_CHECKED_ID ${userIdTokenData.userId}", ExistingWorkPolicy.REPLACE, work)
    }

    class CheckNotificationsWorker(val context: Context, val params: WorkerParameters) :
        CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {

            val json = inputData.getString("USER_ID_TOKEN_DATA")
            if (json != null) {
                val userIdTokenData =
                    Json.decodeFromString<UserIdTokenData>(json)
                GlobalContext.get().get<NotificationsInteractor>().setNotificationsAreChecked(userIdTokenData)
            }

            return Result.success()
        }

    }

}
