package com.studita.data.repository.datasource.users

import com.studita.data.entity.FriendActionRequest
import com.studita.data.entity.IsMyFriendEntity
import com.studita.data.entity.UsersResponse
import com.studita.data.net.UsersService
import com.studita.data.net.connection.ConnectionManager
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import kotlinx.coroutines.CancellationException

class UsersDataStoreImpl(
    private val connectionManager: ConnectionManager,
    private val usersService: UsersService
) : UsersDataStore {

    override suspend fun tryGetUsers(
        userId: Int,
        friendOfUserId: Int?,
        perPage: Int,
        pageNumber: Int,
        sortBy: String?,
        startsWith: String?
    ): Pair<Int, UsersResponse?> {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val result = usersService.getUsers(
                    userId,
                    friendOfUserId,
                    perPage,
                    pageNumber,
                    sortBy,
                    startsWith
                )
                return result.code() to result.body()
            } catch (e: Exception) {
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }
    }

    override suspend fun tryCheckIsMyFriend(myId: Int, userId: Int): Pair<Int, IsMyFriendEntity?> {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val result = usersService.checkIsMyFriend(myId, userId)
                return result.code() to result.body()
            } catch (e: Exception) {
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }
    }

    override suspend fun trySendFriendship(friendActionRequest: FriendActionRequest): Int {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val result = usersService.sendFriendship(friendActionRequest)
                return result.code()
            } catch (e: Exception) {
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }
    }

    override suspend fun tryRemoveFriend(friendActionRequest: FriendActionRequest): Int {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val result = usersService.removeFriend(friendActionRequest)
                return result.code()
            } catch (e: Exception) {
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }
    }

    override suspend fun tryAcceptFriendship(friendActionRequest: FriendActionRequest): Int {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val result = usersService.acceptFriendship(friendActionRequest)
                return result.code()
            } catch (e: Exception) {
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }
    }

    override suspend fun tryRejectFriendship(friendActionRequest: FriendActionRequest): Int {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val result = usersService.rejectFriendship(friendActionRequest)
                return result.code()
            } catch (e: Exception) {
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }
    }

    override suspend fun tryCancelFriendship(friendActionRequest: FriendActionRequest): Int {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val result = usersService.cancelFriendship(friendActionRequest)
                return result.code()
            } catch (e: Exception) {
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }
    }

    override suspend fun tryCheckHasFriends(userId: Int): Pair<Int, Boolean?> {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val result = usersService.hasFriends(userId)
                return result.code() to result.body()
            } catch (e: Exception) {
                if (e is CancellationException)
                    throw e
                else
                    throw ServerUnavailableException()
            }
        }
    }

}