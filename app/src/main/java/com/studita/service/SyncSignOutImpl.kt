package com.studita.service

import android.content.Context
import androidx.work.*
import com.studita.domain.entity.SignOutRequestData
import com.studita.domain.service.SyncSignOut
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.studita.domain.interactor.authorization.AuthorizationInteractor
import com.studita.domain.interactor.user_data.UserDataInteractor
import org.koin.core.context.GlobalContext

class SyncSignOutImpl : SyncSignOut {


    companion object {
        private const val SYNC_SIGN_OUT_ID = "syncSignOutId"
    }

    override fun scheduleSignOut(signOutRequestData: SignOutRequestData) {
        val data = Data.Builder()
        data.putString("SIGN_OUT_DATA", serializeSignOutData(signOutRequestData))

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val work = OneTimeWorkRequest.Builder(SignOutWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()
        val workManager = WorkManager.getInstance(GlobalContext.get().get())
        workManager.enqueueUniqueWork(SYNC_SIGN_OUT_ID, ExistingWorkPolicy.REPLACE, work)
    }

    class SignOutWorker(val context: Context, val params: WorkerParameters) :
        CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {

            val json = inputData.getString("SIGN_OUT_DATA")
            if (json != null) {
                val signOutRequestData =
                    deserializeSignOutData(json)
                GlobalContext.get().get<AuthorizationInteractor>().signOut(signOutRequestData)
            }

            return Result.success()
        }

        private fun deserializeSignOutData(json: String): SignOutRequestData {
            return Gson().fromJson(json, TypeToken.get(SignOutRequestData::class.java).type)
        }

    }

    private fun serializeSignOutData(signOutRequestData: SignOutRequestData): String {
        return Gson().toJson(signOutRequestData)
    }

}
