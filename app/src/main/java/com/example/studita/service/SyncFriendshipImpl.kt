package com.example.studita.service

import android.content.Context
import androidx.work.*
import com.example.studita.di.NetworkModule
import com.example.studita.di.data.SubscribeEmailModule
import com.example.studita.di.data.UsersModule
import com.example.studita.domain.entity.FriendActionRequestData
import com.example.studita.domain.interactor.FriendActionStatus
import com.example.studita.domain.service.SyncFriendship
import com.example.studita.utils.UserUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SyncFriendshipImpl : SyncFriendship{

    companion object {
        private const val SYNC_FRIENDSHIP_ID = "syncFriendshipId"
    }


    override fun scheduleFriendAction(friendActionRequestData: FriendActionRequestData, friendActionType: SyncFriendship.FriendActionType) {
        val data = Data.Builder()
        data.putInt("FRIEND_ACTION_TYPE", friendActionType.ordinal)
        data.putString("FRIEND_ACTION_REQUEST_DATA", serializeFriendActionRequestData(friendActionRequestData))

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val work = OneTimeWorkRequest.Builder(SyncFriendshipWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()
        val workManager = WorkManager.getInstance(NetworkModule.context)
        workManager.enqueueUniqueWork(SYNC_FRIENDSHIP_ID, ExistingWorkPolicy.REPLACE, work)
    }


    class SyncFriendshipWorker(val context: Context, val params: WorkerParameters) : CoroutineWorker(context, params){
        override suspend fun doWork(): Result {
            val json = inputData.getString("FRIEND_ACTION_REQUEST_DATA")
            val friendActionType = SyncFriendship.FriendActionType.values()[inputData.getInt("FRIEND_ACTION_TYPE", 0)]

            val friendActionRequestData = json?.let { deserializeFriendActionRequestData(it) }
            friendActionRequestData?.let {
                when(friendActionType){
                    SyncFriendship.FriendActionType.ADD -> UsersModule.getUsersInteractorImpl().addFriend(friendActionRequestData)
                    SyncFriendship.FriendActionType.REMOVE -> UsersModule.getUsersInteractorImpl().removeFriend(friendActionRequestData)
                    SyncFriendship.FriendActionType.ACCEPT_REQUEST -> UsersModule.getUsersInteractorImpl().acceptFriendship(friendActionRequestData)
                    SyncFriendship.FriendActionType.REJECT_REQUEST -> UsersModule.getUsersInteractorImpl().rejectFriendship(friendActionRequestData)
                }
            }

            return Result.success()
        }

        private fun deserializeFriendActionRequestData(json: String): FriendActionRequestData {
            return Gson().fromJson(json, TypeToken.get(FriendActionRequestData::class.java).type)
        }

    }

    private fun serializeFriendActionRequestData(friendActionRequestData: FriendActionRequestData): String{
        return Gson().toJson(friendActionRequestData)
    }

}