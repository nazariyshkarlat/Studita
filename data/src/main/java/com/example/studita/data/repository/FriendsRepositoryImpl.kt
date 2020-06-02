package com.example.studita.data.repository

import com.example.studita.data.entity.FriendActionRequest
import com.example.studita.data.entity.mapper.FriendActionRequestEntityMapper
import com.example.studita.data.entity.mapper.FriendEntityMapper
import com.example.studita.data.entity.mapper.UserIdTokenMapper
import com.example.studita.data.repository.datasource.friends.FriendsDataStoreFactory
import com.example.studita.domain.entity.FriendActionRequestData
import com.example.studita.domain.entity.FriendData
import com.example.studita.domain.entity.FriendsResponseData
import com.example.studita.domain.entity.UserIdTokenData
import com.example.studita.domain.repository.FriendsRepository

class FriendsRepositoryImpl(private val friendsDataStoreFactory: FriendsDataStoreFactory,
                            private val userIdTokenMapper: UserIdTokenMapper,
                            private val friendEntityMapper: FriendEntityMapper,
                            private val friendActionRequestEntityMapper: FriendActionRequestEntityMapper) : FriendsRepository {
    override suspend fun getFriends(userId: Int, startIndex: Int, endIndex: Int): Pair<Int, FriendsResponseData?> {
        val pair = friendsDataStoreFactory.create().tryGetFriends(userId, startIndex, endIndex)
        val friendsRequest = pair.second
        return pair.first to friendsRequest?.let{FriendsResponseData(friendsRequest.friendsCount, ArrayList(friendsRequest.friends.map{friendEntityMapper.map(it)}))}
    }

    override suspend fun checkIsMyFriend(userId: Int, anotherUserId: Int): Pair<Int, Boolean?> {
        return friendsDataStoreFactory.create().tryCheckIsMyFriend(userId, anotherUserId)
    }

    override suspend fun addFriend(friendActionRequestData: FriendActionRequestData): Int {
        return friendsDataStoreFactory.create().tryAddFriend(friendActionRequestEntityMapper.map(friendActionRequestData))
    }

    override suspend fun removeFriend(friendActionRequestData: FriendActionRequestData): Int {
        return friendsDataStoreFactory.create().tryRemoveFriend(friendActionRequestEntityMapper.map(friendActionRequestData))
    }

}