package com.example.studita.data.repository.datasource.users

import com.example.studita.data.entity.*
import com.example.studita.data.net.UsersService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import java.lang.Exception

class UsersDataStoreImpl(private val connectionManager: ConnectionManager, private val usersService: UsersService): UsersDataStore{

    override suspend fun tryGetUsers(userId: Int, friendOfUserId: Int?, perPage: Int, pageNumber: Int, sortBy: String?, startsWith: String?): Pair<Int, UsersResponse?> {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
                val result = usersService.getUsers(userId, friendOfUserId, perPage, pageNumber, sortBy, startsWith)
                return result.code() to result.body()
        }
    }

    override suspend fun tryCheckIsMyFriend(myId: Int, userId: Int) : Pair<Int, Boolean?>{
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val result = usersService.checkIsMyFriend(myId, userId)
                return result.code() to result.body()
            } catch (e: Exception) {
                throw ServerUnavailableException()
            }
        }
    }

    override suspend fun tryAddFriend(friendActionRequest: FriendActionRequest): Int {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            val result = usersService.addFriend(friendActionRequest)
            return result.code()
        }
    }

    override suspend fun tryRemoveFriend(friendActionRequest: FriendActionRequest): Int {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            val result = usersService.removeFriend(friendActionRequest)
            return result.code()
        }
    }

    override suspend fun tryAcceptFriendship(friendActionRequest: FriendActionRequest): Int {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            val result = usersService.acceptFriendship(friendActionRequest)
            return result.code()
        }
    }

    override suspend fun tryRejectFriendship(friendActionRequest: FriendActionRequest): Int {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            val result = usersService.rejectFriendship(friendActionRequest)
            return result.code()
        }
    }

}