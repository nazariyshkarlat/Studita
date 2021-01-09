package com.studita.service

import android.content.Context
import androidx.work.*
import com.studita.domain.entity.FriendActionRequestData
import com.studita.domain.service.SyncFriendship
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.studita.domain.interactor.user_data.UserDataInteractor
import com.studita.domain.interactor.users.UsersInteractor
import org.koin.core.context.GlobalContext

class SyncFriendshipImpl : SyncFriendship {

    companion object {
        private const val SYNC_FRIENDSHIP_ID = "syncFriendshipId"
    }


    override fun scheduleFriendAction(
        friendActionRequestData: FriendActionRequestData,
        friendActionType: SyncFriendship.FriendActionType
    ) {
        val data = Data.Builder()
        data.putInt("FRIEND_ACTION_TYPE", friendActionType.ordinal)
        data.putString(
            "FRIEND_ACTION_REQUEST_DATA",
            serializeFriendActionRequestData(friendActionRequestData)
        )

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val work = OneTimeWorkRequest.Builder(SyncFriendshipWorker::class.java)
            .setConstraints(constraints)
            .setInputData(data.build())
            .build()
        val workManager = WorkManager.getInstance(GlobalContext.get().get())
        workManager.enqueueUniqueWork("$SYNC_FRIENDSHIP_ID${friendActionRequestData.userIdToken.userId} ${friendActionRequestData.friendId}", ExistingWorkPolicy.REPLACE, work)
    }


    class SyncFriendshipWorker(val context: Context, val params: WorkerParameters) :
        CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            val json = inputData.getString("FRIEND_ACTION_REQUEST_DATA")
            val friendActionType =
                SyncFriendship.FriendActionType.values()[inputData.getInt("FRIEND_ACTION_TYPE", 0)]

            val friendActionRequestData = json?.let { deserializeFriendActionRequestData(it) }
            friendActionRequestData?.let {
                with(GlobalContext.get().get<UsersInteractor>()) {
                    when (friendActionType) {
                        SyncFriendship.FriendActionType.ADD -> sendFriendship(
                            friendActionRequestData
                        )
                        SyncFriendship.FriendActionType.REMOVE -> removeFriend(
                            friendActionRequestData
                        )
                        SyncFriendship.FriendActionType.ACCEPT_REQUEST -> acceptFriendship(
                            friendActionRequestData
                        )
                        SyncFriendship.FriendActionType.REJECT_REQUEST -> rejectFriendship(
                            friendActionRequestData
                        )
                        SyncFriendship.FriendActionType.CANCEL_REQUEST -> cancelFriendship(
                            friendActionRequestData
                        )
                    }
                }
            }

            return Result.success()
        }

        private fun deserializeFriendActionRequestData(json: String): FriendActionRequestData {
            return Gson().fromJson(json, TypeToken.get(FriendActionRequestData::class.java).type)
        }

    }

    private fun serializeFriendActionRequestData(friendActionRequestData: FriendActionRequestData): String {
        return Gson().toJson(friendActionRequestData)
    }

}