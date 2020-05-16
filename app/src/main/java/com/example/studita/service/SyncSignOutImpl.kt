package com.example.studita.service

import android.content.Context
import androidx.work.*
import com.example.studita.di.NetworkModule
import com.example.studita.di.data.AuthorizationModule
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.service.SyncSignOut
import com.example.studita.utils.UserUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SyncSignOutImpl: SyncSignOut{


    companion object {
        private const val SYNC_SIGN_OUT_ID = "syncSubscribeEmailId"
    }

    override fun scheduleSignOut() {
        val data = Data.Builder()
        data.putString("USER_ID_TOKEN_DATA",
            UserUtils.getUserIDTokenData()?.let { serializeUserIdTokenData(it) })

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val work = OneTimeWorkRequest.Builder(SignOutWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()
        val workManager = WorkManager.getInstance(NetworkModule.context)
        workManager.enqueueUniqueWork(SYNC_SIGN_OUT_ID, ExistingWorkPolicy.REPLACE, work)
    }

    class SignOutWorker(val context: Context, val params: WorkerParameters) : CoroutineWorker(context, params){
        override suspend fun doWork(): Result {

            val json = inputData.getString("USER_ID_TOKEN_DATA")
            if(json != null) {
                val userIdToken =
                    deserializeUserIdTokenData(json)
                AuthorizationModule.getAuthorizationInteractorImpl().signOut(userIdToken)
            }

            return Result.success()
        }

        private fun deserializeUserIdTokenData(json: String): UserIdTokenData {
            return Gson().fromJson(json, TypeToken.get(UserIdTokenData::class.java).type)
        }

    }

    private fun serializeUserIdTokenData(userIdTokenData: UserIdTokenData): String{
        return Gson().toJson(userIdTokenData)
    }

}
