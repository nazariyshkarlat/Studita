package com.studita.data.repository

import com.studita.data.entity.toBusinessEntity
import com.studita.data.entity.toRawEntity
import com.studita.data.repository.datasource.users.UsersDataStoreFactory
import com.studita.domain.entity.FriendActionRequestData
import com.studita.domain.entity.IsMyFriendData
import com.studita.domain.entity.UsersResponseData
import com.studita.domain.repository.UsersRepository

class UsersRepositoryImpl(private val usersDataStoreFactory: UsersDataStoreFactory) :
    UsersRepository {
    override suspend fun getUsers(
        userId: Int,
        friendOfUserId: Int?,
        perPage: Int,
        pageNumber: Int,
        sortBy: UsersRepository.SortBy?,
        startsWith: String?
    ): Pair<Int, UsersResponseData?> {
        val pair = usersDataStoreFactory.create()
            .tryGetUsers(userId, friendOfUserId, perPage, pageNumber, sortBy?.toRaw(), startsWith)
        val friendsRequest = pair.second
        return pair.first to friendsRequest?.let {
            UsersResponseData(
                friendsRequest.usersCount,
                ArrayList(friendsRequest.users.map { it.toBusinessEntity() })
            )
        }
    }

    override suspend fun checkIsMyFriend(myId: Int, userId: Int): Pair<Int, IsMyFriendData?> {
        val pair = usersDataStoreFactory.create().tryCheckIsMyFriend(myId, userId)
        return pair.first to pair.second?.toBusinessEntity()
    }

    override suspend fun sendFriendship(friendActionRequestData: FriendActionRequestData): Int {
        return usersDataStoreFactory.create()
            .trySendFriendship(friendActionRequestData.toRawEntity())
    }

    override suspend fun removeFriend(friendActionRequestData: FriendActionRequestData): Int {
        return usersDataStoreFactory.create().tryRemoveFriend(friendActionRequestData.toRawEntity())
    }

    override suspend fun cancelFriendship(friendActionRequestData: FriendActionRequestData): Int {
        return usersDataStoreFactory.create()
            .tryCancelFriendship(friendActionRequestData.toRawEntity())
    }


    override suspend fun acceptFriendship(friendActionRequestData: FriendActionRequestData): Int {
        return usersDataStoreFactory.create()
            .tryAcceptFriendship(friendActionRequestData.toRawEntity())
    }

    override suspend fun rejectFriendship(friendActionRequestData: FriendActionRequestData): Int {
        return usersDataStoreFactory.create()
            .tryRejectFriendship(friendActionRequestData.toRawEntity())
    }

    override suspend fun hasFriends(userId: Int) =
        usersDataStoreFactory.create().tryCheckHasFriends(userId)

}