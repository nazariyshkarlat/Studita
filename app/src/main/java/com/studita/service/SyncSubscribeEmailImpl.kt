package com.studita.service

import android.content.Context
import androidx.work.*
import com.studita.domain.entity.UserIdTokenData
import com.studita.domain.interactor.SubscribeEmailResultStatus
import com.studita.domain.service.SyncSubscribeEmail
import com.studita.presentation.view_model.SingleLiveEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.studita.App
import com.studita.domain.interactor.UserDataStatus
import com.studita.domain.interactor.subscribe_email.SubscribeEmailInteractor
import com.studita.domain.interactor.user_data.UserDataInteractor
import com.studita.utils.PrefsUtils
import com.studita.utils.UserUtils
import org.koin.core.context.GlobalContext

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
        val workManager = WorkManager.getInstance(GlobalContext.get().get())
        workManager.enqueueUniqueWork("$SYNC_SUBSCRIBE_EMAIL_ID ${userIdTokenData.userId}", ExistingWorkPolicy.REPLACE, work)
    }

    class SubscribeEmailWorker(val context: Context, val params: WorkerParameters) :
        CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            val json = inputData.getString("USER_ID_TOKEN_DATA")
            val subscribe = inputData.getBoolean("SUBSCRIBE", false)

            val userIdToken = json?.let { deserializeUserIdTokenData(it) }
            userIdToken?.let {
                with(GlobalContext.get().get<SubscribeEmailInteractor>()) {
                    val result = if (subscribe)
                        subscribe(it)
                    else
                        unsubscribe(it)

                    if(result is SubscribeEmailResultStatus.Success){
                        val localUserData = GlobalContext.get().get<UserDataInteractor>().getUserData(it.userId, getFromLocalStorage = true, true)
                        if(localUserData is UserDataStatus.Success)
                            GlobalContext.get().get<UserDataInteractor>().saveUserData(
                                localUserData.result.apply {
                                    isSubscribed = subscribe
                                }
                            )
                    }

                    if(syncSubscribeEmailLiveData.hasActiveObservers())
                        syncSubscribeEmailLiveData.postValue(result)
                    else {
                        saveSyncedResult(result)
                    }
                }
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
