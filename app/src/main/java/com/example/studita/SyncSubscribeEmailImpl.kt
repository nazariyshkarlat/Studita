package com.example.studita

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.example.studita.di.NetworkModule
import com.example.studita.di.data.SubscribeEmailModule
import com.example.studita.domain.interactor.SubscribeEmailResultStatus
import com.example.studita.domain.service.SyncSubscribeEmail
import com.example.studita.utils.UserUtils

class SyncSubscribeEmailImpl : SyncSubscribeEmail{


    companion object {
        private const val syncSubscribeEmailId = "syncSubscribeEmailId"
        var syncSubscribeEmailLiveData: MutableLiveData<SubscribeEmailResultStatus>? = null
    }

    override fun scheduleSubscribeEmail(subscribe: Boolean){
        val data = Data.Builder()
        data.putBoolean("SUBSCRIBE", subscribe)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val work = OneTimeWorkRequest.Builder(SubscribeEmailWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()
        val workManager = WorkManager.getInstance(NetworkModule.context)
        workManager.enqueueUniqueWork(syncSubscribeEmailId, ExistingWorkPolicy.REPLACE, work)
    }

    class SubscribeEmailWorker(val context: Context, val params: WorkerParameters) : CoroutineWorker(context, params){
        override suspend fun doWork(): Result {
            val userIdToken = UserUtils.getUserTokenIdData()
            val subscribe = inputData.getBoolean("SUBSCRIBE", false)

            userIdToken?.let {
                val result = if (subscribe)
                    SubscribeEmailModule.getSubscribeEmailInteractorImpl().subscribe(it)
                else
                    SubscribeEmailModule.getSubscribeEmailInteractorImpl().unsubscribe(it)

                if(syncSubscribeEmailLiveData == null)
                    SubscribeEmailModule.getSubscribeEmailInteractorImpl().saveSyncedResult(result)
                else
                    syncSubscribeEmailLiveData?.postValue(result)
            }

            return Result.success()
        }

    }

}
