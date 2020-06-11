package com.example.studita.domain.interactor.users

import com.example.studita.domain.entity.FriendActionRequestData
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.CheckIsMyFriendStatus
import com.example.studita.domain.interactor.FriendActionStatus
import com.example.studita.domain.interactor.GetUsersStatus
import com.example.studita.domain.repository.UsersRepository

class UsersInteractorImpl(private val repository: UsersRepository,
                          override val defSortBy: UsersRepository.SortBy = UsersRepository.SortBy.NEW_TO_OLD
) : UsersInteractor{
    override suspend fun getUsers(friendOfUserId: Int?, perPage: Int, pageNumber: Int, userId: Int, sortBy: UsersRepository.SortBy?, startsWith: String?): GetUsersStatus  =
            try {
                val pair = repository.getUsers(userId, friendOfUserId, perPage, pageNumber, sortBy, startsWith)
                val code = pair.first
                val friendsResponseData = pair.second
                when (code) {
                    200 -> if(friendsResponseData!!.users.isNotEmpty()) GetUsersStatus.Success(friendsResponseData) else GetUsersStatus.NoUsersFound
                    else -> GetUsersStatus.Failure
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is NetworkConnectionException) {
                    GetUsersStatus.NoConnection
                }else
                    GetUsersStatus.ServiceUnavailable
            }

    override suspend fun checkIsMyFriend(myId: Int, userId: Int): CheckIsMyFriendStatus  =
            try {
                val pair = repository.checkIsMyFriend(myId, userId)
                val code = pair.first
                val isMyFriend = pair.second
                when (code) {
                    200 -> if(isMyFriend == true) CheckIsMyFriendStatus.IsMyFriend(userId) else CheckIsMyFriendStatus.IsNotMyFriend(userId)
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