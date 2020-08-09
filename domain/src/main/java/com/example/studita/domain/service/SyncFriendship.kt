package com.example.studita.domain.service

import com.example.studita.domain.entity.FriendActionRequestData

interface SyncFriendship {

    fun scheduleFriendAction(
        friendActionRequestData: FriendActionRequestData,
        friendActionType: FriendActionType
    )

    enum class FriendActionType {
        ADD,
        REMOVE,
        CANCEL_REQUEST,
        ACCEPT_REQUEST,
        REJECT_REQUEST
    }
}