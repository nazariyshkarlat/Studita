package com.example.studita.domain.service

import com.example.studita.domain.entity.FriendActionRequestData
import com.example.studita.domain.interactor.FriendActionStatus

interface SyncFriendship {

    fun scheduleFriendAction(friendActionRequestData: FriendActionRequestData, friendActionType: FriendActionType)

    enum class FriendActionType{
        ADD,
        REMOVE,
        ACCEPT_REQUEST,
        REJECT_REQUEST
    }
}