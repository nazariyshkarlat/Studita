package com.example.studita.domain.interactor.friends

import com.example.studita.domain.entity.FriendActionRequestData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.interactor.CheckIsMyFriendStatus
import com.example.studita.domain.interactor.FriendActionStatus
import com.example.studita.domain.interactor.GetFriendsStatus

interface FriendsInteractor {

    suspend fun getFriends(userId: Int, startIndex: Int, endIndex: Int): GetFriendsStatus

    suspend fun checkIsMyFriend(userId: Int, anotherUserId: Int): CheckIsMyFriendStatus

    suspend fun addFriend(friendActionRequestData: FriendActionRequestData): FriendActionStatus

    suspend fun removeFriend(friendActionRequestData: FriendActionRequestData): FriendActionStatus
}