package com.example.studita.domain.interactor.users

import com.example.studita.domain.entity.FriendActionRequestData
import com.example.studita.domain.entity.toStatus
import com.example.studita.domain.exception.NetworkConnectionException
import com.example.studita.domain.interactor.IsMyFriendStatus
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

    override suspend fun checkIsMyFriend(myId: Int, userId: Int): IsMyFriendStatus  =
            try {
                val pair = repository.checkIsMyFriend(myId, userId)
                val code = pair.first
                val isMyFriendData = pair.second
                when (code) {
                    200 -> isMyFriendData!!.toStatus(userId)
                    else -> IsMyFriendStatus.Failure
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is NetworkConnectionException) {
                    IsMyFriendStatus.NoConnection
                }else
                    IsMyFriendStatus.ServiceUnavailable
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

    override suspend fun acceptFriendship(friendActionRequestData: FriendActionRequestData): FriendActionStatus  =
        try {
            when (repository.acceptFriendship(friendActionRequestData)) {
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

    override suspend fun rejectFriendship(friendActionRequestData: FriendActionRequestData): FriendActionStatus  =
        try {
            when (repository.rejectFriendship(friendActionRequestData)) {
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