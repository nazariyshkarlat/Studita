package com.example.studita.service

import android.content.Context
import androidx.work.*
import com.example.studita.di.NetworkModule
import com.example.studita.di.data.PrivacySettingsModule
import com.example.studita.domain.entity.EditDuelsExceptionsRequestData
import com.example.studita.domain.entity.PrivacySettingsRequestData
import com.example.studita.domain.service.SyncPrivacySettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SyncPrivacySettingsImpl : SyncPrivacySettings {

    companion object {
        private const val SYNC_EDIT_PRIVACY_SETTINGS_ID = "syncEditPrivacySettingsId"
        private const val SYNC_EDIT_DUELS_EXCEPTIONS_ID = "syncEditDuelsExceptionsId"
    }

    override fun scheduleEditPrivacySettings(privacySettingsRequestData: PrivacySettingsRequestData) {
        val data = Data.Builder()
        data.putString(
            "EDIT_PRIVACY_SETTINGS_REQUEST_DATA",
            serializeEditPrivacySettingsRequestData(privacySettingsRequestData)
        )

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val work = OneTimeWorkRequest.Builder(SyncEditPrivacySettingsWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()
        val workManager = WorkManager.getInstance(NetworkModule.context)
        workManager.enqueueUniqueWork(
            SYNC_EDIT_PRIVACY_SETTINGS_ID,
            ExistingWorkPolicy.REPLACE,
            work
        )
    }

    override fun scheduleEditDuelsExceptions(editDuelsExceptionsRequestData: EditDuelsExceptionsRequestData) {
        val data = Data.Builder()
        data.putString(
            "EDIT_DUELS_EXCEPTIONS_REQUEST_DATA",
            serializeEditDuelsExceptionsRequestData(editDuelsExceptionsRequestData)
        )

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val work = OneTimeWorkRequest.Builder(SyncEditDuelsExceptionsWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()
        val workManager = WorkManager.getInstance(NetworkModule.context)
        workManager.enqueueUniqueWork(
            SYNC_EDIT_DUELS_EXCEPTIONS_ID,
            ExistingWorkPolicy.REPLACE,
            work
        )
    }

    class SyncEditPrivacySettingsWorker(val context: Context, val params: WorkerParameters) :
        CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            val json = inputData.getString("EDIT_PRIVACY_SETTINGS_REQUEST_DATA")

            val editPrivacySettingsRequestData =
                json?.let { deserializeEditPrivacySettingsRequestData(it) }
            editPrivacySettingsRequestData?.let {
                PrivacySettingsModule.getPrivacySettingsInteractorImpl().editPrivacySettings(it)
            }

            return Result.success()
        }

        private fun deserializeEditPrivacySettingsRequestData(json: String): PrivacySettingsRequestData {
            return Gson().fromJson(json, TypeToken.get(PrivacySettingsRequestData::class.java).type)
        }

    }

    class SyncEditDuelsExceptionsWorker(val context: Context, val params: WorkerParameters) :
        CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            val json = inputData.getString("EDIT_DUELS_EXCEPTIONS_REQUEST_DATA")

            val editDuelsExceptionsRequestData =
                json?.let { deserializeEditDuelsExceptionsRequestData(it) }
            editDuelsExceptionsRequestData?.let {
                PrivacySettingsModule.getPrivacySettingsInteractorImpl().editDuelsExceptions(it)
            }

            return Result.success()
        }

        private fun deserializeEditDuelsExceptionsRequestData(json: String): EditDuelsExceptionsRequestData {
            return Gson().fromJson(
                json,
                TypeToken.get(EditDuelsExceptionsRequestData::class.java).type
            )
        }
    }

    private fun serializeEditDuelsExceptionsRequestData(editDuelsExceptionsRequestData: EditDuelsExceptionsRequestData): String {
        return Gson().toJson(editDuelsExceptionsRequestData)
    }

    private fun serializeEditPrivacySettingsRequestData(editPrivacySettingsRequestData: PrivacySettingsRequestData): String {
        return Gson().toJson(editPrivacySettingsRequestData)
    }


}