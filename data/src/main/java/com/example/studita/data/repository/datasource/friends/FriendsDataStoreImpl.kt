package com.example.studita.data.repository.datasource.friends

import com.example.studita.data.entity.*
import com.example.studita.data.net.FriendsService
import com.example.studita.data.net.PrivacySettingsService
import com.example.studita.data.net.connection.ConnectionManager
import com.example.studita.domain.entity.FriendsResponseData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.exception.ServerUnavailableException
import java.lang.Exception

class FriendsDataStoreImpl(private val connectionManager: ConnectionManager, private val friendsService: FriendsService): FriendsDataStore{

    override suspend fun tryGetFriends(userId: Int, startIndex: Int, endIndex: Int): Pair<Int, FriendsResponse?> {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
                val result = friendsService.getFriends(userId, startIndex, endIndex)
                return result.code() to result.body()
        }
    }

    override suspend fun tryCheckIsMyFriend(userId: Int, anotherUserId: Int) : Pair<Int, Boolean?>{
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            try {
                val result = friendsService.checkIsMyFriend(userId, anotherUserId)
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
            val result = friendsService.addFriend(friendActionRequest)
            return result.code()
        }
    }

    override suspend fun tryRemoveFriend(friendActionRequest: FriendActionRequest): Int {
        if (connectionManager.isNetworkAbsent()) {
            throw NetworkConnectionException()
        } else {
            val result = friendsService.removeFriend(friendActionRequest)
            return result.code()
        }
    }

}