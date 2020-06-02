package com.example.studita.domain.interactor.friends

import com.example.studita.domain.entity.FriendActionRequestData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.CheckIsMyFriendStatus
import com.example.studita.domain.interactor.FriendActionStatus
import com.example.studita.domain.interactor.GetFriendsStatus
import com.example.studita.domain.repository.FriendsRepository

class FriendsInteractorImpl(private val repository: FriendsRepository) : FriendsInteractor{
    override suspend fun getFriends(userId: Int, startIndex: Int, endIndex: Int): GetFriendsStatus  =
            try {
                val pair = repository.getFriends(userId, startIndex, endIndex)
                val code = pair.first
                val friendsResponseData = pair.second
                when (code) {
                    200 -> if(friendsResponseData!!.friends.isNotEmpty()) GetFriendsStatus.Success(friendsResponseData) else GetFriendsStatus.NoFriendsFound
                    else -> GetFriendsStatus.Failure
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is NetworkConnectionException) {
                    GetFriendsStatus.NoConnection
                }else
                    GetFriendsStatus.ServiceUnavailable
            }

    override suspend fun checkIsMyFriend(userId: Int, anotherUserId: Int): CheckIsMyFriendStatus  =
            try {
                val pair = repository.checkIsMyFriend(userId, anotherUserId)
                val code = pair.first
                val isMyFriend = pair.second
                when (code) {
                    200 -> if(isMyFriend == true) CheckIsMyFriendStatus.IsMyFriend else CheckIsMyFriendStatus.IsNotMyFriend
                    else -> CheckIsMyFriendStatus.Failure
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is NetworkConnectionException) {
                    CheckIsMyFriendStatus.NoConnection
                }else
                    CheckIsMyFriendStatus.ServiceUnavailable
            }

    override suspend fun addFriend(friendActionRequestData: FriendActionRequestData): FriendActionStatus  =
            try {
                when (repository.addFriend(friendActionRequestData)) {
                    200 -> FriendActionStatus.Success
                    else -> FriendActionStatus.Failure
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is NetworkConnectionException) {
                    FriendActionStatus.NoConnection
                }else
                    FriendActionStatus.ServiceUnavailable
            }

    override suspend fun removeFriend(friendActionRequestData: FriendActionRequestData): FriendActionStatus  =
            try {
                when (repository.removeFriend(friendActionRequestData)) {
                    200 -> FriendActionStatus.Success
                    else -> FriendActionStatus.Failure
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is NetworkConnectionException) {
                    FriendActionStatus.NoConnection
                }else
                    FriendActionStatus.ServiceUnavailable
            }

}