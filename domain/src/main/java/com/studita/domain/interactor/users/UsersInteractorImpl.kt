package com.studita.domain.interactor.users

import com.studita.domain.entity.FriendActionRequestData
import com.studita.domain.entity.toStatus
import com.studita.domain.exception.NetworkConnectionException
import com.studita.domain.exception.ServerUnavailableException
import com.studita.domain.interactor.FriendActionStatus
import com.studita.domain.interactor.GetUsersStatus
import com.studita.domain.interactor.HasFriendsStatus
import com.studita.domain.interactor.IsMyFriendStatus
import com.studita.domain.repository.UsersRepository
import com.studita.domain.service.SyncFriendship
import kotlinx.coroutines.delay

class UsersInteractorImpl(
    private val repository: UsersRepository,
    override val defSortBy: UsersRepository.SortBy = UsersRepository.SortBy.NEW_TO_OLD,
    private val syncFriendship: SyncFriendship
) : UsersInteractor {

    private val retryDelay = 1000L

    override suspend fun getUsers(
        friendOfUserId: Int?,
        perPage: Int,
        pageNumber: Int,
        userId: Int,
        sortBy: UsersRepository.SortBy?,
        startsWith: String?,
        retryCount: Int
    ): GetUsersStatus =
        try {
            val pair =
                repository.getUsers(userId, friendOfUserId, perPage, pageNumber, sortBy, startsWith)
            val code = pair.first
            val friendsResponseData = pair.second
            when (code) {
                200 -> if (friendsResponseData!!.users.isNotEmpty()) GetUsersStatus.Success(
                    friendsResponseData
                ) else GetUsersStatus.NoUsersFound
                else -> GetUsersStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException) {
                        GetUsersStatus.NoConnection
                    } else
                        GetUsersStatus.ServiceUnavailable
                } else {
                    delay(retryDelay)
                    getUsers(
                        friendOfUserId,
                        perPage,
                        pageNumber,
                        userId,
                        sortBy,
                        startsWith,
                        retryCount - 1
                    )
                }
            } else
                GetUsersStatus.Failure
        }

    override suspend fun checkIsMyFriend(
        myId: Int,
        userId: Int,
        retryCount: Int
    ): IsMyFriendStatus =
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
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException)
                        IsMyFriendStatus.NoConnection
                    else
                        IsMyFriendStatus.ServiceUnavailable
                } else {
                    delay(retryDelay)
                    checkIsMyFriend(myId, userId, retryCount - 1)
                }
            } else
                IsMyFriendStatus.Failure
        }

    override suspend fun sendFriendship(
        friendActionRequestData: FriendActionRequestData,
        retryCount: Int
    ): FriendActionStatus =
        try {
            when (repository.sendFriendship(friendActionRequestData)) {
                200 -> FriendActionStatus.Success
                else -> FriendActionStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if(e is NetworkConnectionException) {
                        syncFriendship.scheduleFriendAction(
                            friendActionRequestData,
                            SyncFriendship.FriendActionType.ADD
                        )
                        FriendActionStatus.NoConnection
                    }else
                        FriendActionStatus.ServiceUnavailable
                }
                else {
                    delay(retryDelay)
                    sendFriendship(friendActionRequestData, retryCount - 1)
                }
            } else
                FriendActionStatus.Failure
        }

    override suspend fun removeFriend(
        friendActionRequestData: FriendActionRequestData,
        retryCount: Int
    ): FriendActionStatus =
        try {
            when (repository.removeFriend(friendActionRequestData)) {
                200 -> FriendActionStatus.Success
                else -> FriendActionStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if(e is NetworkConnectionException) {
                        syncFriendship.scheduleFriendAction(
                            friendActionRequestData,
                            SyncFriendship.FriendActionType.REMOVE
                        )
                        FriendActionStatus.NoConnection
                    }else
                        FriendActionStatus.ServiceUnavailable
                }
                else {
                    delay(retryDelay)
                    removeFriend(friendActionRequestData, retryCount - 1)
                }
            } else
                FriendActionStatus.Failure
        }

    override suspend fun cancelFriendship(
        friendActionRequestData: FriendActionRequestData,
        retryCount: Int
    ): FriendActionStatus =
        try {
            when (repository.cancelFriendship(friendActionRequestData)) {
                200 -> FriendActionStatus.Success
                else -> FriendActionStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if(e is NetworkConnectionException) {
                        syncFriendship.scheduleFriendAction(
                            friendActionRequestData,
                            SyncFriendship.FriendActionType.CANCEL_REQUEST
                        )
                        FriendActionStatus.NoConnection
                    }else
                        FriendActionStatus.ServiceUnavailable
                }
                else {
                    delay(retryDelay)
                    cancelFriendship(friendActionRequestData, retryCount - 1)
                }
            } else
                FriendActionStatus.Failure
        }

    override suspend fun acceptFriendship(
        friendActionRequestData: FriendActionRequestData,
        retryCount: Int
    ): FriendActionStatus =
        try {
            when (repository.acceptFriendship(friendActionRequestData)) {
                200 -> FriendActionStatus.Success
                else -> FriendActionStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if(e is NetworkConnectionException) {
                        syncFriendship.scheduleFriendAction(
                            friendActionRequestData,
                            SyncFriendship.FriendActionType.ACCEPT_REQUEST
                        )
                        FriendActionStatus.NoConnection
                    }else
                        FriendActionStatus.ServiceUnavailable
                }
                else {
                    delay(retryDelay)
                    acceptFriendship(friendActionRequestData, retryCount - 1)
                }
            } else
                FriendActionStatus.Failure
        }

    override suspend fun rejectFriendship(
        friendActionRequestData: FriendActionRequestData,
        retryCount: Int
    ): FriendActionStatus =
        try {
            when (repository.rejectFriendship(friendActionRequestData)) {
                200 -> FriendActionStatus.Success
                else -> FriendActionStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if(e is NetworkConnectionException) {
                        syncFriendship.scheduleFriendAction(
                            friendActionRequestData,
                            SyncFriendship.FriendActionType.REJECT_REQUEST
                        )
                        FriendActionStatus.NoConnection
                    }else
                        FriendActionStatus.ServiceUnavailable
                }
                else {
                    delay(retryDelay)
                    rejectFriendship(friendActionRequestData, retryCount - 1)
                }
            } else
                FriendActionStatus.Failure
        }

    override suspend fun hasFriends(userId: Int, retryCount: Int): HasFriendsStatus =
        try {
            val result = repository.hasFriends(userId)
            when (result.first) {
                200 -> if (result.second == true) HasFriendsStatus.HasFriends else HasFriendsStatus.HasNoFriends
                else -> HasFriendsStatus.Failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkConnectionException || e is ServerUnavailableException) {
                if (retryCount == 0) {
                    if (e is NetworkConnectionException) {
                        HasFriendsStatus.NoConnection
                    } else
                        HasFriendsStatus.ServiceUnavailable
                } else {
                    delay(retryDelay)
                    hasFriends(userId, retryCount - 1)
                }
            } else
                HasFriendsStatus.Failure
        }

}