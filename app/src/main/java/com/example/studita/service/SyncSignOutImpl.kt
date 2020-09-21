package com.example.studita.service

import android.content.Context
import androidx.work.*
import com.example.studita.di.NetworkModule
import com.example.studita.di.data.AuthorizationModule
import com.example.studita.domain.entity.SignOutRequestData
import com.example.studita.domain.service.SyncSignOut
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SyncSignOutImpl : SyncSignOut {


    companion object {
        private const val SYNC_SIGN_OUT_ID = "syncSubscribeEmailId"
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
        val workManager = WorkManager.getInstance(NetworkModule.context)
        workManager.enqueueUniqueWork("$SYNC_SIGN_OUT_ID ${signOutRequestData.userIdTokenData.userId}", ExistingWorkPolicy.REPLACE, work)
    }

    class SignOutWorker(val context: Context, val params: WorkerParameters) :
        CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {

            val json = inputData.getString("SIGN_OUT_DATA")
            if (json != null) {
                val signOutRequestData =
                    deserializeSignOutData(json)
                AuthorizationModule.getAuthorizationInteractorImpl().signOut(signOutRequestData)
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
